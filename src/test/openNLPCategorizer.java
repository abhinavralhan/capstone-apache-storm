package test;
 
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
 
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
 
public class openNLPCategorizer {
	DoccatModel model;
 
	public static void main(String[] args) {
		openNLPCategorizer twitterCategorizer = new openNLPCategorizer();
		twitterCategorizer.trainModel();
		twitterCategorizer.classifyNewTweet("i am good");
	}
 
	public void trainModel() {
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream("input/data.txt");
			ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			ObjectStream sampleStream = new DocumentSampleStream(lineStream);
			// Specifies the minimum number of times a feature must be seen
			int cutoff = 2;
			int trainingIterations = 1000;
			model = DocumentCategorizerME.train("en", sampleStream, cutoff,
					trainingIterations);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 
	public void classifyNewTweet(String tweet) {
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
		double[] outcomes = myCategorizer.categorize(tweet);
		String category = myCategorizer.getBestCategory(outcomes);
 
		if (category.equalsIgnoreCase("1")) {
			System.out.println("The tweet is positive :) ");
		}
		else {
			System.out.println("The tweet is negative :( ");
		}
	}
}