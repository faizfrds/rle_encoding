package app;

import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;

public class RLEconverter {
   private final static int DEFAULT_LEN = 100; // used to create arrays.

   /*
    *  This method reads in an uncompressed ascii image file that contains 
    *  2 characters. It stores each line of the file in an array.
    *  It then calls compressAllLines to get an array that stores the compressed
    *  version of each uncompressed line from the file. The compressed array
    *  is then passed to the getCompressedFileStr method which returns a String
    *  of all compressed lines (the two charcaters are written in the first line)
    *  in CSV format. This String is written to a text file with the prefix "RLE_"
    *  added to the original, uncompressed file name.
    *  Note that dataSize keeps track of the number of lines in the file. The array 
    *  that holds the lines of the file is initialized to the DEFAULT_LEN, which 
    *  is assumed to be << the number of lines in the file.
    */   
  public void compressFile(String fileName) throws IOException{
    Scanner scan = new Scanner(new FileReader(fileName));
    String line = null;
    String[] decompressed = new String [DEFAULT_LEN];
    int dataSize = 0;
    while(scan.hasNext()){
      line = scan.next();
      if(line != null && line.length()>0)
        decompressed[dataSize]=line;
        dataSize++;
    }
    scan.close();
    char[] fileChars = discoverAsciiChars(decompressed, dataSize); 
    String[] compressed = compressAllLines(decompressed, dataSize, fileChars);
    writeFile(getCompressedFileStr(compressed, fileChars), "RLE_"+fileName);
  }
  
   
/*
 * This method implements the RLE compression algorithm. It takes a line of uncompressed data
 * from an ascii file and returns the RLE encoding of that line in CSV format.
 * The two characters that make up the image file are passed in as a char array, where
 * the first cell contains the first character that occurred in the file.
*/
  public String compressLine(String line, char[] fileChars){
   //TODO: Implement this method

    String compressed = "";

    for (int i = 0; i < line.length(); i++){

      Integer occurence = 1; //default number of occurence when a char is discovered

      while (i != line.length()-1 && line.charAt(i) == fileChars[0] && line.charAt(i+1) == fileChars[0]){
        occurence++;
        i++;   
      }

      while (i != line.length()-1 && line.charAt(i) == fileChars[1] && line.charAt(i+1) == fileChars[1]){
        occurence++;
        i++;   
      }

      /*if first character in 'line' is not equal to first char in fileChars, then its known that its occurence in the
      string is 0 initially*/
      if (compressed == "" && line.charAt(0)!=fileChars[0]){ 
        compressed = "0," + occurence.toString();
      }

      //ensures unnecessary comma doesnt appear at the start of the line 
      else if(compressed == ""){ 
        compressed = occurence.toString();
      }

      else{
        compressed = compressed + "," + occurence.toString();
      }

    }
    return compressed;
  }

  /*
   *  This method discovers the two ascii characters that make up the image. 
   *  It iterates through all of the lines and writes each compressed line
   *  to a String array which is returned. The method compressLine is called on 
   *  each line.
   *  The dataSize is the number of lines in the file, which is likely to be << the length of lines.
   */

  public String[] compressAllLines(String[] lines, int dataSize, char[] fileChars){
    //TODO: Implement this method

    String[] compressedLines = new String[dataSize];

    for (int i = 0; i < dataSize; i++){
      compressedLines[i] = compressLine(lines[i], fileChars); //calls the compressLine method for each line and adds to the array
    }

    return compressedLines;
  }

/*
 *  This method assembles the lines of compressed data for
 *  writing to a file. The first line must be the 2 ascii characters
 *  in comma-separated format. 
 */
  public String getCompressedFileStr(String[] compressed, char[] fileChars) {
      //TODO: Implement this method

      String fileString = String.valueOf(fileChars[0]) + ',' ;
      fileString = fileString + String.valueOf(fileChars[1]) + "\n"; //the first row stores the two characters

      for (int i = 0; i < compressed.length; i++){

        fileString = fileString + compressed[i];

        if (i != compressed.length-1){ //ensures that new line is not made when the loop is on the last row
          fileString = fileString + "\n";
        }

      }
      return fileString;
  }
   /*
    *  This method reads in an RLE compressed ascii image file that contains 
    *  2 characters. It stores each line of the file in an array.
    *  It then calls decompressAllLines to get an array that stores the decompressed
    *  version of each compressed line from the file. The first row contains the two 
    *  ascii charcaters used in the original image file. The decompressed array
    *  is then passed to the getDecompressedFileStr method which returns a String
    *  of all decompressed lines, thus restoring the original, uncompressed image.
    *  This String is written to a text file with the prefix "DECOMP_"
    *  added to the original, compressed file name.
    *  Note that dataSize keeps track of the number of lines in the file. The array 
    *  that holds the lines of the file is initialized to the DEFAULT_LEN, which 
    *  is assumed to be << the number of lines in the file.
    */   

  public void decompressFile(String fileName) throws IOException{
    Scanner scan = new Scanner(new FileReader(fileName));
    String line = null;
    String[] compressed = new String [DEFAULT_LEN];
    int dataSize =0;
    while(scan.hasNext()){
      line = scan.next();
      if(line != null && line.length()>0)
        compressed[dataSize]=line;
        dataSize++;
    }
    scan.close();
    String[] decompressed = decompressAllLines(compressed, dataSize);
    writeFile(getDecompressedFileStr(decompressed), "DECOMP_"+fileName);
  }
 
   /*
   * This method decodes lines that were encoded by the RLE compression algorithm. 
   * It takes a line of compressed data and returns the decompressed, or original version
   * of that line. The two characters that make up the image file are passed in as a char array, 
   * where the first cell contains the first character that occurred in the file.
   */
    public String decompressLine(String line, char[] fileChars){
      //TODO: Implement this method

      String decompress = "";
      int fileCharsIndex = 1;

      for (int i = 0; i < line.length(); i++){

        //default occurence is -1 because the nested for loop needs this variable to be declared but not always run
        int occurence = -1; 
        
        if (i != line.length()-1 && line.charAt(i) != ',' && line.charAt(i+1) != ','){ //executes when there's two digit occurences
          String twoDigits = String.valueOf(line.charAt(i));
          twoDigits = twoDigits + String.valueOf(line.charAt(i+1));
          occurence = Integer.parseInt(twoDigits);
          fileCharsIndex = (fileCharsIndex == 0)? 1:0; //switches between characters
          i++;
        }

        else if (line.charAt(i) != ','){
          occurence = Integer.parseInt(String.valueOf(line.charAt(i)));
          fileCharsIndex = (fileCharsIndex == 0)? 1:0; //switches between characters
        }

        for (int j = 0; j < occurence; j++){ //adds the character to string according to how many times it occurs
          decompress = decompress + fileChars[fileCharsIndex];
        }
        
      }
      return decompress;
    }
    /*
   *  This method iterates through all of the compressed lines and writes 
   *  each decompressed line to a String array which is returned. 
   *  The method decompressLine is called on each line. The first line in
   *  the compressed array passed in are the 2 ascii characters used to make
   *  up the image. 
   *  The dataSize is the number of lines in the file, which is likely to be << the length of lines.
   *  The array returned contains only the decompressed lines to be written to the decompressed file.
   */
  public String[] decompressAllLines(String[] lines, int dataSize){
     //TODO: Implement this method

    String[] decompressedArray = new String[dataSize];
    char[] fileChars = new char[2];

    fileChars[0] = lines[0].charAt(0); //store the two chars in the fileChars array
    fileChars[1] = lines[0].charAt(2);
 
    for (int j = 1; j < decompressedArray.length; j++){
      decompressedArray[j] = decompressLine(lines[j], fileChars);
    }
    return decompressedArray;
  }
  
  /*
   *  This method assembles the lines of decompressed data for
   *  writing to a file. 
   */
  public String getDecompressedFileStr(String[] decompressed){
    
    String data = "";
   //TODO: Implement this method

    for (int i = 1; i < decompressed.length; i++){
      data = data + decompressed[i];
      data = (i != decompressed.length-1)? data + "\n" : data;
    }
    return data;
  }

  // assume the file contains only 2 different ascii characters.
  public char[] discoverAsciiChars(String[] decompressed, int dataSize){
//TODO: Implement this method
  
  char[] fileChars = new char[2];
  fileChars[0] = decompressed[0].charAt(0);

  for (int i = 1; i < decompressed[0].length(); i++){
    if (decompressed[0].charAt(i) != fileChars[0]){
      fileChars[1] = decompressed[0].charAt(i);
    }
  }
  return fileChars;
} 

  public void writeFile(String data, String fileName) throws IOException{
  PrintWriter pw = new PrintWriter(fileName);
    pw.print(data);
    pw.close();
 }
}