package com.example.signuploginrealtime.QRPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signuploginrealtime.LoginActivity;
import com.example.signuploginrealtime.NavigationBar.HomeFragment;
import com.example.signuploginrealtime.R;
import com.example.signuploginrealtime.SignupActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class RekapActivity extends AppCompatActivity implements RekapAdapter.ItemClickListener, DatabaseHelper.DataCallback {

    private static final int REQUEST_QR_CODE = 1;
    private RecyclerView recyclerView;
    private RekapAdapter adapter;
    private List<DataItem> dataItems;
    private DatabaseHelper databaseHelper;

    private SearchView searchView;
    Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataItems = new ArrayList<>();
        adapter = new RekapAdapter(dataItems, this);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);
        backButton = findViewById(R.id.back_recap);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kembali ke HomeFragment langsung dari RekapActivity
                onBackPressed();
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }

            private void filterData(String newText) {
                List<DataItem> filteredList = new ArrayList<>();
                for (DataItem dataItem : dataItems) {
                    if (dataItem.getItemName().toLowerCase().contains(newText.toLowerCase()) ||
                            dataItem.getDate().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(dataItem);
                    }
                }
                adapter.setDataItems(filteredList);
                adapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RekapActivity.this, QRActivity.class);
                startActivityForResult(intent, REQUEST_QR_CODE);
            }
        });

        Button exportButton = findViewById(R.id.btnExportToExcel);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });
        Button deleteAllButton = findViewById(R.id.btnDeleteAll);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAllConfirmationDialog();
            }
        });

        databaseHelper.getAllData(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QR_CODE && resultCode == RESULT_OK && data != null) {
            String text = data.getStringExtra("text");
            String plainText = data.getStringExtra("plainText");
            String date = data.getStringExtra("date");

            databaseHelper.insertData(text, plainText, date);
        }
    }




    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All Data")
                .setMessage("Are you sure you want to delete all data?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAllData() {
        databaseHelper.deleteAllData();

        dataItems.clear();
        adapter.notifyDataSetChanged();

        Toast.makeText(RekapActivity.this, "All data has been deleted.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        // Cek apakah ada fragment dalam back stack
        if (backStackEntryCount > 0) {
            // Pop semua fragment dari back stack kecuali yang terakhir (HomeFragment)
            for (int i = 0; i < backStackEntryCount - 1; i++) {
                fragmentManager.popBackStack();
            }
            // Kembali ke HomeFragment
            super.onBackPressed();
        } else {
            // Jika tidak ada fragment dalam back stack, tutup activity
            finish();
        }
    }

    @Override
    public void onDataAdded(DataItem dataItem) {
        dataItems.add(dataItem);
        sortDataItemsByDate();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDataChanged(DataItem dataItem) {
        for (int i = 0; i < dataItems.size(); i++) {
            if (dataItems.get(i).getId().equals(dataItem.getId())) {
                dataItems.set(i, dataItem);
                sortDataItemsByDate();
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onDataRemoved(DataItem dataItem) {
        for (int i = 0; i < dataItems.size(); i++) {
            if (dataItems.get(i).getId().equals(dataItem.getId())) {
                dataItems.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        DataItem clickedItem = dataItems.get(position);
        String clickedItemId = clickedItem.getId();
        databaseHelper.deleteData(clickedItemId);
    }

    private void sortDataItemsByDate() {
        Collections.sort(dataItems, (item1, item2) -> {
            long date1 = item1.getDateTimestamp();
            long date2 = item2.getDateTimestamp();
            return Long.compare(date1, date2);
        });
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            exportToExcel(dataItems, startDate, endDate);
        });
        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void exportToExcel(List<DataItem> dataItems, Long startDate, Long endDate) {
        try {
            String strDate = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(new Date());
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FileExcel");
            if (!root.exists()) {
                root.mkdirs();
            }
            File path = new File(root, strDate + ".xlsx");

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data Rekap");

            // Set default cell style for centered alignment and bold text
            CellStyle centeredBoldCellStyle = workbook.createCellStyle();
            centeredBoldCellStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredBoldCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            centeredBoldCellStyle.setFont(boldFont);

            // Merge cells and create headers
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 32));
            Row headerRow1 = sheet.createRow(1);
            Cell headerCell1 = headerRow1.createCell(0);
            headerCell1.setCellValue("RECAP DAILY MAINTENANCE");
            headerCell1.setCellStyle(centeredBoldCellStyle);

            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 32));
            Row headerRow2 = sheet.createRow(2);
            Cell headerCell2 = headerRow2.createCell(0);
            headerCell2.setCellValue("Tanggal " + formatDate(startDate) + " s/d " + formatDate(endDate));
            headerCell2.setCellStyle(centeredBoldCellStyle);

            Row headerRow3 = sheet.createRow(3);
            Cell headerCell3_1 = headerRow3.createCell(0);
            headerCell3_1.setCellValue("No.");
            headerCell3_1.setCellStyle(centeredBoldCellStyle);

            Cell headerCell3_2 = headerRow3.createCell(1);
            headerCell3_2.setCellValue("ITEM CHECK");
            headerCell3_2.setCellStyle(centeredBoldCellStyle);

            // Calculate the number of days between startDate and endDate
            long daysDiff = TimeUnit.MILLISECONDS.toDays(endDate - startDate) + 1;

            for (int i = 0; i < daysDiff; i++) {
                Cell dayCell = headerRow3.createCell(i + 2);
                dayCell.setCellValue(i + 1);
                dayCell.setCellStyle(centeredBoldCellStyle);
            }

            // Map to store information for each item name
            Map<String, Map<Integer, String>> itemInfoMap = new HashMap<>();

            // Fill the map with item information within the specified date range
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            for (DataItem dataItem : dataItems) {
                String itemName = dataItem.getItemName();
                String dateStr = dataItem.getDate(); // Assuming date is in "dd MMM yyyy" format

                // Parse the date string into a Date object
                Date date = dateFormat.parse(dateStr);

                // Check if the date is within the specified range
                long itemDate = date.getTime();
                if (itemDate >= startDate && itemDate <= endDate) {
                    int dayOfMonth = Integer.parseInt(formatDate(itemDate).split("-")[0]);
                    String information = dataItem.getInformation();

                    if (!itemInfoMap.containsKey(itemName)) {
                        itemInfoMap.put(itemName, new HashMap<>());
                    }
                    itemInfoMap.get(itemName).put(dayOfMonth, information);
                }
            }

            // Create rows for data
            int rowNum = 4; // Start from row 4 for data
            int no = 1; // Initialize the row number
            for (String itemName : itemInfoMap.keySet()) {
                Row dataRow = sheet.createRow(rowNum++);
                Cell noCell = dataRow.createCell(0);
                noCell.setCellValue(no++);
                noCell.setCellStyle(centeredBoldCellStyle);

                Cell itemNameCell = dataRow.createCell(1);
                itemNameCell.setCellValue(itemName);
                itemNameCell.setCellStyle(centeredBoldCellStyle);

                Map<Integer, String> infoMap = itemInfoMap.get(itemName);
                for (int i = 0; i < daysDiff; i++) {
                    Cell infoCell = dataRow.createCell(i + 2);
                    if (infoMap.containsKey(i + 1)) {
                        infoCell.setCellValue(infoMap.get(i + 1));
                    }
                    infoCell.setCellStyle(centeredBoldCellStyle);
                }
            }

            // Set column widths
            for (int i = 0; i <= daysDiff + 2; i++) {
                sheet.setColumnWidth(i, 3000);
            }


// Hitung jumlah baris data yang ada
            int numRows = dataItems.size();

// Create row for "Check by"
            int checkByRowNum = numRows + 1; // Sesuaikan dengan jumlah data yang ada
            Row checkByRow = sheet.createRow(checkByRowNum);
            Cell checkByCell = checkByRow.createCell(1);
            checkByCell.setCellValue("Check by : ");
            checkByCell.setCellStyle(centeredBoldCellStyle);

// Create footer rows
            int mengetahuiRowNum = numRows + 4; // Sesuaikan dengan jumlah data yang ada
            Row mengetahuiRow = sheet.createRow(mengetahuiRowNum);
            sheet.addMergedRegion(new CellRangeAddress(mengetahuiRowNum, mengetahuiRowNum, 4, 10));
            Cell mengetahuiCell = mengetahuiRow.createCell(4);
            mengetahuiCell.setCellValue("Mengetahui");
            mengetahuiCell.setCellStyle(centeredBoldCellStyle);

            sheet.addMergedRegion(new CellRangeAddress(mengetahuiRowNum, mengetahuiRowNum, 18, 24));
            Cell diperiksaCell = mengetahuiRow.createCell(18);
            diperiksaCell.setCellValue("Diperiksa Oleh");
            diperiksaCell.setCellStyle(centeredBoldCellStyle);

            int inputNameRowNum = numRows + 7; // Sesuaikan dengan jumlah data yang ada
            Row inputNameRow = sheet.createRow(inputNameRowNum);
            sheet.addMergedRegion(new CellRangeAddress(inputNameRowNum, inputNameRowNum, 4, 10));
            Cell inputNameCellE = inputNameRow.createCell(4);
            inputNameCellE.setCellValue("Input Name");
            inputNameCellE.setCellStyle(centeredBoldCellStyle);

            sheet.addMergedRegion(new CellRangeAddress(inputNameRowNum, inputNameRowNum, 18, 24));
            Cell inputNameCellS = inputNameRow.createCell(18);
            inputNameCellS.setCellValue("Input Name");
            inputNameCellS.setCellStyle(centeredBoldCellStyle);

// Add thick bottom borders to footer rows
            RegionUtil.setBorderBottom(BorderStyle.THICK, new CellRangeAddress(mengetahuiRowNum, mengetahuiRowNum, 4, 10), sheet);
            RegionUtil.setBorderBottom(BorderStyle.THICK, new CellRangeAddress(mengetahuiRowNum, mengetahuiRowNum, 18, 24), sheet);
            RegionUtil.setBorderBottom(BorderStyle.THICK, new CellRangeAddress(inputNameRowNum, inputNameRowNum, 4, 10), sheet);
            RegionUtil.setBorderBottom(BorderStyle.THICK, new CellRangeAddress(inputNameRowNum, inputNameRowNum, 18, 24), sheet);

// Create row for "Engineering Manager" and "Duty Engineering"
            int engManagerRowNum = numRows + 8; // Sesuaikan dengan jumlah data yang ada
            sheet.addMergedRegion(new CellRangeAddress(engManagerRowNum, engManagerRowNum, 4, 10)); // Engineering Manager
            sheet.addMergedRegion(new CellRangeAddress(engManagerRowNum, engManagerRowNum, 18, 24)); // Duty Engineering

            Row engManagerFooterRow = sheet.createRow(engManagerRowNum);
            Cell engManagerFooterCell = engManagerFooterRow.createCell(4);
            engManagerFooterCell.setCellValue("Engineering Manager");
            engManagerFooterCell.setCellStyle(centeredBoldCellStyle); // Apply the centered style

            Row dutyEngFooterRow = sheet.getRow(engManagerRowNum);
            Cell dutyEngFooterCell = dutyEngFooterRow.createCell(18);
            dutyEngFooterCell.setCellValue("Duty Engineering");
            dutyEngFooterCell.setCellStyle(centeredBoldCellStyle); // Apply the centered style

            // Set column widths
            for (int i = 0; i <= 33; i++) {
                sheet.setColumnWidth(i, 3000);
            }


            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(this, "Data berhasil di ekspor!", Toast.LENGTH_SHORT).show();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }



    // Other methods, interfaces, etc.
}
