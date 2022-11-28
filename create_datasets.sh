jar='target/tokenjoin_preprocessing-0.0.1-SNAPSHOT-jar-with-dependencies.jar'
in_dir='/mnt/data/tokenjoin/original/'
out_dir='/mnt/data/tokenjoin/serialized/'

parts=(20 40 60 80 100)

datasets=('yelp' 'gdelt' 'enron')
maxlines=(160016 500000 517431)



for i in "${!datasets[@]}"; do
   name=${datasets[i]}
   maxline=${maxlines[i]}
   logFile="$log_dir$name.log"
   
   for part in "${parts[@]}"; do
       inputFile="$in_dir$name""_clean.csv"
       outputFile="$out_dir$name""_"$part".txt"
       lines=$((part * maxline / 100))
       java -Xms70g -Xmx70g -jar "${jar}" --inputFile "${inputFile}" --outputFile "${outputFile}" --maxLines "${lines}" --totalLines "${maxline}" --keepOriginal false --cleanDuplicates false --serialize 0
   done
   outputFile="$out_dir$name""_100_topk.txt"
   java -Xms70g -Xmx70g -jar "${jar}" --inputFile "${inputFile}" --outputFile "${outputFile}" --maxLines "${maxline}" --totalLines "${maxline}" --keepOriginal false --cleanDuplicates true --serialize 0
done



datasets=('flickr' 'dblp' 'mind')
maxlines=(500000 500000 123130)


for i in "${!datasets[@]}"; do
   name=${datasets[i]}
   maxline=${maxlines[i]}
   logFile="$log_dir$name.log"
   
   for part in "${parts[@]}"; do
       inputFile="$in_dir$name""_clean.csv"
       outputFile="$out_dir$name""_"$part".txt"
       lines=$((part * maxline / 100))
       java -Xms70g -Xmx70g -jar "${jar}" --inputFile "${inputFile}" --outputFile "${outputFile}" --maxLines "${lines}" --totalLines "${maxline}" --keepOriginal true --cleanDuplicates false --serialize 0
   done
   outputFile="$out_dir$name""_100_topk.txt"
   java -Xms70g -Xmx70g -jar "${jar}" --inputFile "${inputFile}" --outputFile "${outputFile}" --maxLines "${maxline}" --totalLines "${maxline}" --keepOriginal true --cleanDuplicates true --serialize 0
done
