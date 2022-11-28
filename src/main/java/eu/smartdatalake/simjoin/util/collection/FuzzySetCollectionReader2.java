package eu.smartdatalake.simjoin.util.collection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Class for loading data from a CSV file with the corresponding config JSON
 * file.
 *
 */
public class FuzzySetCollectionReader2 {
	protected static long over400 = 0;
	protected static final int upperLimit = 400;
	protected static final int lowerLimit = 1;
	protected static final int qgram = 3;

	/**
	 * @param readConfig:      Config (JSON) file of the selected source file
	 *                         containing the correspondent information.
	 * @param execConfig:      Config (JSON) file of the corresponding execution.
	 * @param keepOriginal:    Boolean option for keeping original strings.
	 *                         Necessary for Edit Similarity, Optional for Jaccard
	 * @param cleanDuplicates: Boolean option to clean duplicate records.
	 * @return {@link FuzzyIntSetCollection FuzzyIntSetCollection} collection
	 */
	public List<String> prepareCollection(String file, int maxLines, int totalLines, boolean keepOriginal,
			boolean cleanDuplicates, int serialize) {
		/* READ PARAMETERS */
		// file parsing
		int colSetId = -1;
		int colSetTokens = 0;

		String columnDelimiter = " ";
		String elementDelimiter = ";";
		String tokenDelimiter = null;

		boolean header = false;

		TIntList indices = new TIntArrayList();
		for (int i = 0; i < totalLines; i++)
			indices.add(i);
		indices.shuffle(new Random(1924));

		if (maxLines == -1)
			maxLines = totalLines;
		else
			maxLines = Math.min(maxLines, totalLines);
		TIntList subList = indices.subList(0, maxLines);
		TIntSet sample = new TIntHashSet(subList);
		int maxLine = subList.max();

		double tokensPerElement = 0;
		double elementsPerSet = 0;
		BufferedReader br;
		int lines = -1;
		int errorLines = 0;

//		Map<String, Integer> hashCodes = new HashMap<String, Integer>();

		String line, tempString;
		String[] columns, words;

		ArrayList<String> records = new ArrayList<String>();

		ArrayList<ArrayList<String>> elements = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> elements2 = new ArrayList<ArrayList<String>>();
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> tokens2 = new ArrayList<String>();
		ArrayList<String> original = new ArrayList<String>();
		TObjectIntMap<String> tempTokens = new TObjectIntHashMap<String>();
		TObjectIntMap<String> tempTokens2 = new TObjectIntHashMap<String>();

		try {
			br = new BufferedReader(new FileReader(file));

			// if the file has header, ignore the first line
			if (header) {
				br.readLine();
			}

			while ((line = br.readLine()) != null) {
				lines++;

				if (lines % 10000 == 0) {
					String msg = String.format("Lines read: %d\r", lines);
					System.out.print(msg);
				}

				try {
					if (!sample.contains(lines)) {
						continue;
					}
					
					columns = line.split(columnDelimiter);

					String record_id;
					if (colSetId < 0) {
						record_id = String.valueOf(lines);
					} else
						record_id = columns[colSetId];
					
					words = columns[colSetTokens].toLowerCase().split(elementDelimiter);
					
					// TODO: ADDED HERE ELEMENT DEDUPLICATION
					Set<String> tempWords = new HashSet<String>();
					for (String word : words)
						tempWords.add(word);
					words = new String[tempWords.size()];
					int noWord = 0;
					for (String word : tempWords)
						words[noWord++] = word;
					tempWords.clear();
					
					
					if (serialize == 2 ) {
						String recordString = "";
						for (String word : words) {
							//TODO: REPLACE _ FOR SM
							word = word.replaceAll("_", "#");
							recordString += word + "_";
						}
						recordString.substring(0, recordString.length()-1);
						records.add(recordString);
						continue;
					}
					
					if (words.length < lowerLimit) {
						errorLines++;
						continue;
					}

//					if (words.length > upperLimit) {
//						words = Arrays.copyOf(words, upperLimit); // first 400 words
//						over400++;
//					}

					// Deduplication
					Arrays.sort(words);
					tempString = Arrays.toString(words);

					elements = new ArrayList<ArrayList<String>>();
					original = new ArrayList<String>();
					elements2 = new ArrayList<ArrayList<String>>();

					for (String word : words) {
						//TODO: REPLACE _ FOR SM
						word = word.replaceAll("_", "#");

						if (tokenDelimiter == null) {
							// Max $$ added.
							String word2 = word + StringUtils.repeat('$', qgram - 1);
							// Removed extra $ to fit qchunks
							int leftChars = word2.length() % qgram;
							word2 = word2.substring(0, word2.length() - leftChars);

							tempTokens = new TObjectIntHashMap<String>();
							tokens = new ArrayList<String>();

							String token = word2;
							for (int i = 0; i <= token.length() - qgram; i++) {
								tempTokens.adjustOrPutValue(token.substring(i, i + qgram), 1, 1);
							}

							for (String key : tempTokens.keySet()) {
								for (int val = 0; val < tempTokens.get(key); val++) {
									tokens.add(key + "@" + val);
								}
							}

							if (tokens.size() == 0) {
								continue;
							}

							if (keepOriginal) {
								// Not saved word2, but word
								original.add(word2);

								tempTokens2 = new TObjectIntHashMap<String>();
								tokens2 = new ArrayList<String>();
//								tokens2.clear();
//								tempTokens2.clear();

								String token2 = word2;
								for (int i = 0; i <= token2.length() - qgram; i += qgram) {
									tempTokens2.adjustOrPutValue(token2.substring(i, i + qgram), 1, 1);
								}

								for (String key : tempTokens2.keySet()) {
									for (int val = 0; val < tempTokens2.get(key); val++) {
										tokens2.add(key + "@" + val);
									}
								}
								elements2.add(tokens2);
							}

						} else {
							tokens = new ArrayList<String>();
							for (String token : word.split(tokenDelimiter))
								tokens.add(token);

							if (tokens.size() == 0) {
								continue;
							}

						}

						elements.add(tokens);
						tokensPerElement += tokens.size();
					}
					if (elements.size() < 1) {
						errorLines++;
						continue;
					}

					String recordString = record_id;
					for(ArrayList<String> element : elements) {
						recordString += "_" + element.get(0);
						for (int ti=1; ti<element.size(); ti++) {
							String token = element.get(ti).replace("_", ";").replace(" ", ";");
							recordString += " " + token;
						}
					}
					
					records.add(recordString);
					elementsPerSet += elements.size();

				} catch (Exception e) {
					e.printStackTrace();
					errorLines++;
				}
				if (lines > maxLine) {
					break;
				}
			}

			br.close();
		}catch(

	FileNotFoundException e)
	{
		e.printStackTrace();
	}catch(
	IOException e)
	{
			e.printStackTrace();
		}

	tokensPerElement/=elementsPerSet;elementsPerSet/=records.size();

	System.out.println("Finished reading file. Lines read: "+lines+". Lines skipped due to errors: "+errorLines+". Num of sets: "+records.size()+". Elements per set: "+elementsPerSet+". Tokens per Element: "+tokensPerElement);
//		System.out.println("Over 400 elements were: " + over400);

	return records;
}}