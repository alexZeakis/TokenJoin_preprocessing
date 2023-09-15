# TokenJoin_preprocessing

This repository is part of [TokenJoin](https://github.com/alexZeakis/TokenJoin). It is necessary to run this code in order to preprocess the data needed as input for TokenJoin.

## Usage

**Step 1**. Download or clone the project:
```sh
$ git clone https://github.com/alexZeakis/TokenJoin_preprocessing
```

**Step 2**. Open terminal inside root folder and install by running:
```sh
$ mvn install
```

**Step 3** Make **create_datasets.sh** executable and run it:
```sh
$ ./create_datasets.sh <in_dir> <out_dir>
```

## Datasets
We have used six real-world datasets:

- [Yelp](https://www.yelp.com/dataset): 160,016 sets extracted from the Yelp Open Dataset. Each set refers to a business. Its elements are the categories associated to it.

- [GDELT](https://www.gdeltproject.org/data.html): 500,000 randomly selected sets from January 2019 extracted from the GDELT Project. Each set refers to a news article. Its elements are the themes associated with it. Themes are hierarchical. Each theme is represented by a string concatenating all themes from it to the root of the hierarchy.

- [Enron](https://www.cs.cmu.edu/~enron): 517,431 sets, each corresponding to an email message. The elements are the words contained in the message body.

- [Flickr](https://yahooresearch.tumblr.com/post/89783581601/one-hundred-million-creative-commons-flickr-images-for): 500,000 randomly selected images from the Flickr Creative Commons dataset. Each set corresponds to a photo. The elements are the tags associated to that photo.

- [DBLP](https://dblp.uni-trier.de/xml): 500,000 publications from the DBLP computer science bibliography. Each set refers to a publication. The elements are author names and words in the title.

- [MIND](https://msnews.github.io): 123,130 articles from the MIcrosoft News Dataset. Each set corresponds to an article. The elements are the words in its abstract.

The preprocessed csv versions of the datasets used in the experiments can be found [here](https://drive.google.com/drive/folders/1u9ixJM25koPkHi8FJ0atrHL1WcE8dtLw?usp=sharing).
