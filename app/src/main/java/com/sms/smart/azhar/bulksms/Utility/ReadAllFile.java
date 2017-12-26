package com.sms.smart.azhar.bulksms.Utility;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nanosoft-Android on 12/19/2017.
 */

public class ReadAllFile {


   static List<String> phoneNumbers = new ArrayList<>();
    public static void  readTxtfile(Context context, String filename, ListView listView) {
        phoneNumbers.clear();
        try {
            // Open stream to read file.
            FileInputStream in = new FileInputStream(filename);

            BufferedReader br= new BufferedReader(new InputStreamReader(in));

            StringBuilder sb= new StringBuilder();
            String s= null;
            while((s= br.readLine())!= null)  {
                sb.append(s).append("|");
            }

            // getPhoneNumber(sb.toString());
            GetMobileNos(sb.toString(),listView);

            //Log.e("txt",""+phoneNumbers.get(0));

        } catch (Exception e) {
            Toast.makeText(AppController.getAppContext(),"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public static void getPhoneNumber(String sb) {
        phoneNumbers.clear();


        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(sb);
        //ArrayList<String> allMatches = new ArrayList<>();
        while (m.find()) {
            phoneNumbers.add(m.group());
        }

        Toast.makeText(AppController.getAppContext(), ""+phoneNumbers.get(3).toString()+" size"+phoneNumbers.size(), Toast.LENGTH_SHORT).show();
    }


    public static void GetMobileNos(String sb,ListView listView){
        List<String> list=new ArrayList<>();
        Pattern pattern = Pattern.compile("01[\\d+]+");
        Matcher mr = pattern.matcher(sb);
        while (mr.find()){
            list.add(padRight(mr.group(0),11));
        }

        Toast.makeText(AppController.getAppContext(), ""+list.size(), Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AppController.getAppContext(), android.R.layout.simple_expandable_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s).replace(' ', '0');
    }
    public static void readXLSXFile(String filename,ListView listView) throws IOException
    {
        phoneNumbers.clear();
        FileInputStream ExcelFileToRead = new FileInputStream(filename);
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

        XSSFWorkbook test = new XSSFWorkbook();

        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        XSSFCell cell;

        Iterator rows = sheet.rowIterator();
        String str="";
        while (rows.hasNext())
        {
            row=(XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            while (cells.hasNext())
            {
                cell=(XSSFCell) cells.next();

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                    // str+=cell.getStringCellValue()+" ";
                    //Toast.makeText(this, cell.getStringCellValue()+" ", Toast.LENGTH_SHORT).show();
                }
                else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                    // Toast.makeText(this, cell.getNumericCellValue()+" ", Toast.LENGTH_SHORT).show();
                    // phoneNumbers.add(cell.getNumericCellValue()+" ");
                    str+=cell.getNumericCellValue()+"|";
                    //str+=cell.getNumericCellValue()+"|";
                }
            }
            str+="$";

        }
        GetMobileNos(str,listView);

       // Log.e("XLSXvalue",""+mobiles.get(0));

    }
    public static  void readExcelFile(Context context, String filename,ListView listView) {
        phoneNumbers.clear();
        try{
            // Creating Input Stream
            //File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(filename);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();
            String str="";
            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
//                    phoneNumbers.add(myCell.toString());
//                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                    str+=myCell.toString()+"|";
                }
                str+="$";
            }
           GetMobileNos(str,listView);
            //Log.e("XLSvalue",""+mobiles.get(0));

        }catch (Exception e){e.printStackTrace(); }

    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}



