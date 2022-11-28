package gr.athenarc.imsi.runners;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import gr.athenarc.imsi.simjoin.util.collection.FuzzyIntSetCollection;
import gr.athenarc.imsi.simjoin.util.collection.FuzzySetCollectionReader;
import gr.athenarc.imsi.simjoin.util.collection.FuzzySetCollectionReader2;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;

public class MainRunner {

	public static void main(String[] args) {
		System.out.println(Arrays.toString(args));

		String inputFile = null, outputFile = null;
		int maxLines = 0, totalLines = 0, serialize = 0;
		boolean keepOriginal = false, cleanDuplicates = false;
		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equals("--inputFile"))
				inputFile = args[i + 1];
			if (args[i].equals("--outputFile"))
				outputFile = args[i + 1];

			if (args[i].equals("--maxLines"))
				maxLines = Integer.parseInt(args[i + 1]);
			if (args[i].equals("--totalLines"))
				totalLines = Integer.parseInt(args[i + 1]);
			if (args[i].equals("--serialize"))
				serialize = Integer.parseInt(args[i + 1]);

			if (args[i].equals("--keepOriginal"))
				keepOriginal = Boolean.parseBoolean(args[i + 1]);
			if (args[i].equals("--cleanDuplicates"))
				cleanDuplicates = Boolean.parseBoolean(args[i + 1]);

		}

		System.out.println(maxLines + " " + totalLines);
		System.out.println(keepOriginal + " " + cleanDuplicates + " " + serialize);

		if (serialize == 0) {
			/* EXECUTE THE OPERATION */
			FuzzyIntSetCollection collection = new FuzzySetCollectionReader().prepareCollection(inputFile, maxLines,
					totalLines, keepOriginal, cleanDuplicates);
			
			FileOutputStream fileOutputStream;
			try {
				fileOutputStream = new FileOutputStream(outputFile);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(collection);
				objectOutputStream.flush();
				objectOutputStream.close();
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			List<String> collection = new FuzzySetCollectionReader2().prepareCollection(inputFile, maxLines, totalLines,
					keepOriginal, cleanDuplicates, serialize);

			try {
				FileWriter myWriter = new FileWriter(outputFile);
				for (String line : collection)
					myWriter.write(line + "\n");
				myWriter.close();
				System.out.println("Successfully wrote to the file.");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
	}
}