package org.apache.asterix.external.feed.ml.tools;

import org.apache.asterix.external.feed.ml.tools.textanalysis.Features;
import org.apache.asterix.external.feed.ml.tools.textanalysis.TextAnalyzer;
import org.apache.asterix.external.feed.ml.tools.textanalysis.TextAnalyzer.Term;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.awt.SystemColor.text;

public class ArffWriter {

    public ArffWriter() {}

    public String createRelation(String relation) {
        return "@relation " + relation;
    }

    public String createAttribute(String name, String values) {
        return "@attribute " + name + " " + values;
    }

    public String createAttributes(List<String> features) {
        String result = "";
        for (String feature : features) {
            result += '\n';
            result += "@attribute ";
            result += feature;
        }
        return result;
    }

    public String createData(List<String> data) {
        String result = "\n@data";
        for (String values : data) {
            result += '\n';
            result += values;
        }
        return result;
    }

    public void writeArff(List<String> lines) {
        String filePath = "/Users/thormartin/asterix-machine-learning/src/main/resources/data/twitter.arff";
        Path file = Paths.get(filePath);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {e.printStackTrace();}
    }


    public static void main(String[] args) {

        List<String> featuresList = new ArrayList<>();
        TextAnalyzer analyzer = new TextAnalyzer();
        Features features = new Features();

        String negative = "/Users/thormartin/asterix-machine-learning/src/main/resources/data/twitter-data/negative.txt";
        String positive = "/Users/thormartin/asterix-machine-learning/src/main/resources/data/twitter-data/positive.txt";

        for (String file : Arrays.asList(negative, positive)) {

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                while ((line = br.readLine()) != null) {
                    analyzer.analyze(line);
                    Term tokens[] = analyzer.getTerms();
                    features.check(tokens);
                    String classValue = file.substring(80,88);
                    featuresList.add(features.getValues() + "," + classValue);
                }

            } catch (IOException e) {e.printStackTrace();}

        }
        ArffWriter a = new ArffWriter();
        String relation = a.createRelation("tweets");
        String attributes = a.createAttributes(features.getFeatures());
        String classAttribute = a.createAttribute("class", "{positive, negative}");
        String data = a.createData(featuresList);
        List<String> lines = Arrays.asList(relation, attributes, classAttribute, data);
        a.writeArff(lines);

    }
}
