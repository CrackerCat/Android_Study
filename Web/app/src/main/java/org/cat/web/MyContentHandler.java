package org.cat.web;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static android.content.ContentValues.TAG;

/**
 * Created by lixiong on 2017/6/26.
 */

public class MyContentHandler extends DefaultHandler {
    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    //开始解析的时候调用
    @Override
    public void startDocument() throws SAXException {
        //super.startDocument();
        id = new StringBuilder();
        name = new StringBuilder();
        version= new StringBuilder();

    }

    //开始解析某个节点的时候调用
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        //记录当前结点名
        nodeName = localName;
    }

    //获取节点中内容的时候调用
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //super.characters(ch, start, length);
        //根据当前结点名判断将内容添加到哪一个StringBuilder对象中
        if ("id".equals(nodeName)){
            id.append(ch, start, length);
        }else if ("name".equals(nodeName)){
            name.append(ch, start, length);
        }else if ("version".equals(nodeName)){
            version.append(ch, start, length);
        }
    }

    //完成解析某个节点的时候调用
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //super.endElement(uri, localName, qName);
        if ("app".equals(localName)){
            Log.d(TAG, "endElement: id is " + id.toString().trim());
            Log.d(TAG, "endElement: name is " + name.toString().trim());
            Log.d(TAG, "endElement: verdion is " + version.toString().trim());
            //最后要将StringBuilder清空掉
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    //完成整个xml解析的时候调用
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

}
