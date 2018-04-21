package test;
 

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.storm.shade.org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
 
public class openNLPCategorizer {
	DoccatModel model;
 
	public static void main(String args[]) 
	{
		openNLPCategorizer cat = new openNLPCategorizer();
		//cat.trainModel();
		System.out.println(cat.classifyNewTweet("I hate coffee"));
	}
	
	@SuppressWarnings("deprecation")
	public void trainModel(URL url) {
		InputStream dataIn = null;
		try {

			dataIn = IOUtils.toInputStream(Resources.toString(url, Charsets.UTF_8));
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
 
	public int classifyNewTweet(String tweet) {
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
		double[] outcomes = myCategorizer.categorize(tweet);
		String category = myCategorizer.getBestCategory(outcomes);
		if (category.equalsIgnoreCase("1")) {
			return 1;
		}
		else {
			return 0;
		}
	}
}