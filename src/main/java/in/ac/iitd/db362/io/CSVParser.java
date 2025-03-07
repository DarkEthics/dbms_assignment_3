package in.ac.iitd.db362.io;

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import in.ac.iitd.db362.catalog.Catalog;
import in.ac.iitd.db362.index.Index;
import in.ac.iitd.db362.index.bplustree.BPlusTreeIndex;
import in.ac.iitd.db362.index.hashindex.ExtendibleHashing;
import in.ac.iitd.db362.index.BitmapIndex;

/**
 * CSVParser parses a CSV file where the first line is a header.
 * Each header field is of the form "attributeName:attributeType" (e.g. salary:double).
 * The delimiter is specified as a parameter.
 *
 * The API accepts a map that specifies, for each attribute, which index types to create.
 * Supported index types include "BPlusTree", "Hash", and "Bitmap".
 * As rows are read, values are converted to the appropriate type and inserted into the index.
 */
public class CSVParser {

    // Helper class to store header metadata.
    private static class ColumnMeta {
        String name;
        String type; // "integer", "double", "string", or "date"

        ColumnMeta(String name, String type) {
            this.name = name;
            this.type = type.toLowerCase();
        }
    }

    /**
     * Parses the CSV file and builds indexes.
     *
     * @param filePath         The path to the CSV file.
     * @param delimiter        The delimiter (e.g. ",").
     * @param indexesToCreate  A map where the key is an attribute name and the value is a list
     *                         of index type names to create for that attribute.
     * @param maxRowId         Maximum row id (used to create Bitmap indexes).
     */
    public static void parseCSV(String filePath, String delimiter, Catalog catalog, Map<String, List<String>> indexesToCreate, int maxRowId) {
        List<ColumnMeta> columns = new ArrayList<>();
        // Use ISO_LOCAL_DATE for date parsing.
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("Empty CSV file.");
                return;
            }
            // Parse header; expected format for each token: "attributeName:attributeType"
            String[] headerTokens = headerLine.split(delimiter);
            for (String token : headerTokens) {
                String[] parts = token.split(":");
                if (parts.length != 2) {
                    System.err.println("Invalid header format for token: " + token);
                    continue;
                }
                String attrName = parts[0].trim();
                String attrType = parts[1].trim();
                columns.add(new ColumnMeta(attrName, attrType));
            }

            // For each attribute specified in indexesToCreate, create index instances and add them to the catalog.
            // We use the static catalog from BooleanQueryParser.
            for (ColumnMeta col : columns) {
                if (indexesToCreate.containsKey(col.name)) {
                    List<String> indexTypes = indexesToCreate.get(col.name);
                    for (String idxType : indexTypes) {
                        idxType = idxType.trim();
                        switch (col.type) {
                            case "integer":
                                // Use Integer for integer type.
                                if (idxType.equalsIgnoreCase("BPlusTree")) {
                                    catalog.addIndex(col.name, new BPlusTreeIndex<Integer>(col.name));
                                } else if (idxType.equalsIgnoreCase("Hash")) {
                                    catalog.addIndex(col.name, new ExtendibleHashing<Integer>(col.name));
                                } else if (idxType.equalsIgnoreCase("Bitmap")) {
                                    catalog.addIndex(col.name, new BitmapIndex<Integer>(col.name, maxRowId));
                                }
                                break;
                            case "double":
                                // Use Double for double type.
                                if (idxType.equalsIgnoreCase("BPlusTree")) {
                                    catalog.addIndex(col.name, new BPlusTreeIndex<Double>(col.name));
                                } else if (idxType.equalsIgnoreCase("Hash")) {
                                    catalog.addIndex(col.name, new ExtendibleHashing<Double>(col.name));
                                } else if (idxType.equalsIgnoreCase("Bitmap")) {
                                    catalog.addIndex(col.name, new BitmapIndex<Double>(col.name, maxRowId));
                                }
                                break;
                            case "string":
                                if (idxType.equalsIgnoreCase("BPlusTree")) {
                                    catalog.addIndex(col.name, new BPlusTreeIndex<String>(col.name));
                                } else if (idxType.equalsIgnoreCase("Hash")) {
                                    catalog.addIndex(col.name, new ExtendibleHashing<String>(col.name));
                                } else if (idxType.equalsIgnoreCase("Bitmap")) {
                                    catalog.addIndex(col.name, new BitmapIndex<String>(col.name, maxRowId));
                                }
                                break;
                            case "date":
                                if (idxType.equalsIgnoreCase("BPlusTree")) {
                                    catalog.addIndex(col.name, new BPlusTreeIndex<LocalDate>(col.name));
                                } else if (idxType.equalsIgnoreCase("Hash")) {
                                    catalog.addIndex(col.name, new ExtendibleHashing<LocalDate>(col.name));
                                } else if (idxType.equalsIgnoreCase("Bitmap")) {
                                    catalog.addIndex(col.name, new BitmapIndex<LocalDate>(col.name, maxRowId));
                                }
                                break;
                            default:
                                System.err.println("Unsupported attribute type: " + col.type);
                        }
                    }
                }
            }

            // Process each row and insert values into the corresponding indexes.
            int rowId = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines
                String[] tokens = line.split(delimiter);
                for (int i = 0; i < tokens.length && i < columns.size(); i++) {
                    ColumnMeta col = columns.get(i);
                    if (indexesToCreate.containsKey(col.name)) {
                        String rawValue = tokens[i].trim();

                        List<Index> indexes = catalog.getIndexes(col.name);
                        for (Index idx : indexes) {

                            try {
                                switch (col.type) {
                                    case "integer":
                                        //int convertedValue = Integer.parseInt(rawValue);
                                        idx.insert(Integer.parseInt(rawValue), rowId);
                                        break;
                                    case "double":
                                        //double convertedValue = Double.parseDouble(rawValue);
                                        idx.insert(Double.parseDouble(rawValue), rowId);
                                        break;
                                    case "date":
                                        //LocalDate convertedValue = LocalDate.parse(rawValue, dateFormatter);
                                        idx.insert(LocalDate.parse(rawValue, dateFormatter), rowId);
                                        break;
                                    case "string":
                                        //convertedValue = rawValue;
                                        idx.insert(rawValue, rowId);
                                        break;
                                    default:
                                        System.err.println("Unsupported type for conversion: " + col.type);
                                }
                            } catch (Exception e) {
                                System.err.println("Error converting value '" + rawValue + "' for attribute " + col.name);
                                continue;
                            }
                        }

                        // For each index already created for this attribute, insert the value.
                       /* List<Index> indexes = catalog.getIndexes(col.name);
                        for (Index idx : indexes) {
                            // The index is generic; we assume the conversion type is correct.
                           // @SuppressWarnings("unchecked")
                            //Index<Object> index = (Index<Object>) idx;
                            idx.insert(convertedValue.getClass().getTypeName(), rowId);
                        }*/
                    }
                }
                rowId++;
            }
            System.out.println("CSV parsing complete. Total rows processed: " + rowId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
