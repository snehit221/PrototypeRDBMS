package query.core;

import utils.ConstantKeywords;

import java.util.ArrayList;
import java.util.List;

public class SelectQueryPrinter {

/*    public static void main(String[] args) {
        List<String> selectQueryInterimBuffer = new ArrayList<>();
        selectQueryInterimBuffer.add("employee_id|$|first_name|$|last_name|$|hire_date");
        selectQueryInterimBuffer.add("1|$|John|$|Doe|$|2023-10-19");
        selectQueryInterimBuffer.add("2|$|Jane|$|Smith|$|2023-10-20");
        selectQueryInterimBuffer.add("3|$|Alice|$|Johnson|$|2023-10-21");

        System.out.println("Select Query Result:");
        //printSelectQueryResult(selectQueryInterimBuffer);
    }*/

    /**
     * responsible for printing the select query operation
     *
     * @param selectQueryInterimBuffer
     */
    public void printSelectQueryResult(List<String> selectQueryInterimBuffer) {
        if (selectQueryInterimBuffer.isEmpty()) {
            System.out.println("No data found for the executed query");
            return;
        }
        //System.out.println("select qyery buffer:" + selectQueryInterimBuffer);
        String[] headers = selectQueryInterimBuffer.get(0).split(ConstantKeywords.columnSplitRetrievalDelimiter);
        int numberOfColumns = headers.length;

        int[] columnWidths = new int[numberOfColumns];
        String[][] tableData = new String[selectQueryInterimBuffer.size()][numberOfColumns];

        for (int i = 0; i < selectQueryInterimBuffer.size(); i++) {
            String[] rowData = selectQueryInterimBuffer.get(i).split(ConstantKeywords.columnSplitRetrievalDelimiter);

            for (int j = 0; j < numberOfColumns; j++) {
                tableData[i][j] = rowData[j];
                if (rowData[j].length() > columnWidths[j]) {
                    columnWidths[j] = rowData[j].length();
                }
            }
        }
        // column info row
        for (int j = 0; j < numberOfColumns; j++) {
            System.out.printf("%-" + (columnWidths[j] + 2) + "s", headers[j]);
        }
        System.out.println();

        for (int i = 1; i < selectQueryInterimBuffer.size(); i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                System.out.printf("%-" + (columnWidths[j] + 2) + "s", tableData[i][j]);
            }
            System.out.println();
        }
    }

}
