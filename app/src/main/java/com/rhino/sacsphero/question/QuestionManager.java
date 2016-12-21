package com.rhino.sacsphero.question;


import android.content.Context;
import android.content.res.AssetManager;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class QuestionManager {

    public static ArrayList<Question> GetAllQuestions(Context context) {
        ArrayList<Question> questions = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open("questions.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            QuestionXmlHandler questionXmlHandler = new QuestionXmlHandler();
            xr.setContentHandler(questionXmlHandler);
            InputSource inputSource = new InputSource(inputStream);
            xr.parse(inputSource);
            questions = questionXmlHandler.getQuestions();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return questions;
    }
}
