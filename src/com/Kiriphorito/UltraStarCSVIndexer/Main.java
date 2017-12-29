package com.Kiriphorito.UltraStarCSVIndexer;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class Main {

    public static void main(String[] args) throws Exception{
        switch(args.length){
            case 0:
                System.out.println("There were no arguments?!");
                break;
            case 1:
                // Find if the directory exists
                Path path = Paths.get(args[0]);
                if (Files.exists(path) == false){
                    System.out.println("Directory doesn't exist or something is wrong!");
                    break;
                }

                // Thanks to
                // https://stackoverflow.com/questions/1384947/java-find-txt-files-in-specified-folder

                // Get the list of directories and sub-directories
                List<File> directories = new ArrayList<>();
                directories.add(new File(args[0]));

                // Get the list of files with extension .txt
                List<File> textFiles = new ArrayList<>();

                // Specify the extensions
                List<String> extensions = new ArrayList<>();
                extensions.add("*.txt");

                // Create filter
                FileFilter typeFilter = new WildcardFileFilter(extensions);

                while (directories.isEmpty() == false)
                {
                    List<File> subDirectories = new ArrayList();

                    for(File f : directories)
                    {
                        subDirectories.addAll(Arrays.asList(f.listFiles((FileFilter)DirectoryFileFilter.INSTANCE)));
                        textFiles.addAll(Arrays.asList(f.listFiles(typeFilter)));
                    }
                    directories.clear();
                    directories.addAll(subDirectories);
                }

                // Separates the folder names into two arrays. One being song names and the other being artists
                List<String> songNames = new ArrayList<>();
                List<String> artists = new ArrayList<>();
                List<String> origin = new ArrayList<>();

                // Adds song data for each file
                for (File textFile : textFiles){
                    Scanner s = new Scanner(textFile);
                    ArrayList<String> lines = new ArrayList<String>();
                    while (s.hasNextLine()){
                        lines.add(s.nextLine());
                    }
                    s.close();

                    boolean originFound = false;
                    boolean artistFound = false;
                    for(String line : lines){
                        // For some strange unknown reason : still appear so used replace to get rid of them
                        if (line.toLowerCase().indexOf("#title:") != -1){
                            songNames.add("\"" + line.substring(7).replace(":","") + "\"");
                            //System.out.println(songNames.get(songNames.size()-1));
                        } else if (line.toLowerCase().indexOf("#artist:") != -1){
                            artists.add("\"" + line.substring(8).replace(":","") + "\"");
                            artistFound = true;
                            //System.out.println(artists.get(artists.size()-1));
                        } else if (line.toLowerCase().indexOf("#origin:") != -1) {
                            System.out.println("Found origin!");
                            origin.add("\"" + line.substring(8).replace(":", "") + "\"");
                            System.out.println("\"" + line.substring(8).replace(":", "") + "\"");
                            originFound = true;
                        }
                    }
                    // After testing, I don't know why yet but, some files cannot be read in properly!?
                    if (artistFound == false){
                        String fileName = textFile.getName();
                        System.out.println(fileName);
                        int index = fileName.indexOf("-");
                        System.out.println(fileName.substring(0, index - 1));
                        artists.add("\"" + fileName.substring(0, index - 1) + "\"");
                        System.out.println(fileName.substring(index + 2 , fileName.length() - 4));
                        songNames.add("\"" + fileName.substring(index + 2 , fileName.length() - 4) + "\"");
                        origin.add("TXT ERROR");
                        continue;
                    }
                    // If there is no ORIGIN information in the file
                    if (originFound == false){
                        origin.add("N/A");
                    }
                }

                System.out.println("Number of Song Names: " + songNames.size());
                System.out.println("Number of Artist: " + artists.size());
                System.out.println("Number of Origin: " + origin.size());

                // Write to CSV files
                FileWriter fileWriter = null;
                String COMMA_DELIMITER = ",";
                String NEW_LINE_SEPARATOR = "\n";
                String FILE_HEADER = "song_name,artist,origin";

                try {
                    fileWriter = new FileWriter(args[0]+"/songs.csv");

                    // Header
//                    fileWriter.append(FILE_HEADER.toString());
//                    fileWriter.append(NEW_LINE_SEPARATOR);

                    for (int x = 0; x < songNames.size(); x++){
                        fileWriter.append(songNames.get(x));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(artists.get(x));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(origin.get(x));
                        fileWriter.append(NEW_LINE_SEPARATOR);
                    }

                } catch (Exception e) {
                    System.out.println("Error in CsvFileWriter !!!");
                    e.printStackTrace();
                } finally {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        System.out.println("Error while flushing/closing fileWriter !!!");
                        e.printStackTrace();
                    }
                }
                break;
            default:
                System.out.println("There were too many arguments");
                break;
        }
    }
}