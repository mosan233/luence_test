package com.sf.lucene.first;

import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.management.Query;
import java.io.File;
import java.io.IOException;

public class IndexManager {

    private IndexWriter indexWriter;

    @Before
    public void init() throws Exception {
        indexWriter = new IndexWriter(FSDirectory.open(new File("D:\\教程\\javaee\\10 Lucene\\lucene\\02.参考资料\\indexresp").toPath())
                ,new IndexWriterConfig(new IKAnalyzer()));
    }


    @Test
    public void addDocument() throws IOException {
        Document document = new Document();
        Field nameField = new TextField("name","王者荣耀",Field.Store.YES);
        Field contentField = new TextField("content","钟无艳，百里守约",Field.Store.YES);
        Field sizeFieldValue = new LongPoint("size",333l);
        Field sizeFieldStore = new StoredField("size",333l);
        document.add(nameField);
        document.add(contentField);
        document.add(sizeFieldValue);
        document.add(sizeFieldStore);
        indexWriter.addDocument(document);
    }

    @Test
    public void deleteAllDocument() throws IOException {
        indexWriter.deleteAll();
    }

    @Test
    public void deleteQueryDocument() throws IOException{
        indexWriter.deleteDocuments(new Term("content","钟无艳"));
    }


    @Test
    public void updateDocument() throws Exception{
        Document document = new Document();
        Field nameField = new TextField("name","王者宝典",Field.Store.YES);
        Field contentField = new TextField("content","今天折叠害虾子",Field.Store.YES);
        document.add(nameField);
        document.add(contentField);
        indexWriter.updateDocument(new Term("content","spring"), document);
    }



    @After
    public void destroy() throws Exception{
        indexWriter.close();
    }
}
