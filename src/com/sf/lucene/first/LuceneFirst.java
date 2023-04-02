package com.sf.lucene.first;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class LuceneFirst {

    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    @Before
    public void init() throws Exception{
        FSDirectory directory = FSDirectory.open(new File("D:\\教程\\javaee\\10 Lucene\\lucene\\02.参考资料\\indexresp").toPath());
        indexReader = DirectoryReader.open(directory);
        indexSearcher = new IndexSearcher(indexReader);
    }

    @Test
    public void createIndex() throws Exception{
        //1.创建一个Director对象，指定索引库保存的位置
        Directory directory = FSDirectory.open(new File("D:\\教程\\javaee\\10 Lucene\\lucene\\02.参考资料\\indexresp").toPath());
        // 2.基于Director对象创建一个IndexWriter对象
//        IndexWriter indexWriter = new IndexWriter(directory,new IndexWriterConfig());
        IndexWriter indexWriter = new IndexWriter(directory,new IndexWriterConfig(new IKAnalyzer()));
        //3.读取磁盘上的文件，对应每个文件创建一个文档对象
        File baseFile = new File("D:\\教程\\javaee\\10 Lucene\\lucene\\02.参考资料\\searchsource");
        File[] files = baseFile.listFiles();
        for (File file : files){
            String fileName = file.getName();
            String filePath = file.getPath();
            String content = FileUtils.readFileToString(file, "utf8");
            long fileSize = FileUtils.sizeOf(file);
            //4.向文档对象中添加域
            Field fieldName = new TextField("name",fileName,Field.Store.YES);
//            Field fieldPath = new TextField("path",filePath,Field.Store.YES);
            Field fieldPath = new StoredField("path",filePath);
            Field fieldContent = new TextField("content",content, Field.Store.YES);
//            Field fieldSize = new TextField("size",fileSize+"",Field.Store.YES);
            Field fieldSizeValue = new LongPoint("size",fileSize);
            Field fieldSizeStored = new StoredField("size",fileSize);
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
//            document.add(fieldSize);
            document.add(fieldSizeValue);
            document.add(fieldSizeStored);
            //5.把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //6.关闭IndexWriter对象
        indexWriter.close();
    }

    @Test
    public void searchIndex() throws Exception{
        //1.创建一个Director对象，指定索引库的位置
        Directory directory = FSDirectory.open(new File("D:\\教程\\javaee\\10 Lucene\\lucene\\02.参考资料\\indexresp").toPath());
        //2.基于Director对象创建一个IndexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3.基于IndexReader对象创建一个IndexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4.创建一个Query对象，TermQuery Term的两个参数（要查找的域，关键词）
//        Query query = new TermQuery(new Term("content","spring"));
        Query query = new TermQuery(new Term("content","折叠"));
        //5.执行查询，得到一个TopDocs对象 第2参数表示此次查询的记录数
        TopDocs topDocs = indexSearcher.search(query, 10);
        //6.去查询结果的总记录数
        System.out.println("本次查询的总记录数："+topDocs.totalHits);
        //7.取文档列表
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //获取文档ID,然后根据文档id获取Document对象
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            //8.打印文档内容
            String name = doc.get("name");
            String path = doc.get("path");
            String size = doc.get("size");
            System.out.println("name:"+name);
            System.out.println("path:"+path);
            System.out.println("size:"+size);
            System.out.println("=================");
        }
        //9.关闭IndexReader对象
        indexReader.close();
    }

    @Test
    public void testTokenStream() throws Exception{
        //1.创建一个StandardAnalyzer对象，标准分析器
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //2.使用分析器对象的tokenStream方法获得一个TokenStream对象
//        TokenStream tokenStream = analyzer.tokenStream("", "Lucene is an open source library for full-text search and retrieval. Supported and provided by the Apache Software Foundation, Lucene provides a simple yet powerful application programming interface (API) for full-text indexing.");
        TokenStream tokenStream = analyzer.tokenStream("", "Lucene是一套用于全文检索和括弧笑搜寻的开源程序库,由Apache软件基金会支持和提供 Lucene提供了一个简单却强大的应用程序接口(API),能够做全文索引.");
        //3.向TokenStream对象中设置一个引用，相当一个指针。
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //4.调用TokenStream对象的reset方法，不调用会报异常
        tokenStream.reset();
        //5.使用while循环遍历TokenStream对象
        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        //6.关闭TokenStream对象
        tokenStream.close();
    }


    /**
     * 范围查询
     */
    @Test
    public void testRangeQuery() throws Exception{
        Query query = LongPoint.newRangeQuery("size", 800, 1000);
        TopDocs topDocs = indexSearcher.search(query, 10);
        showResult(topDocs);
    }

    private void showResult(TopDocs topDocs)throws Exception {
        System.out.println("总记录数："+topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            String name = doc.get("name");
            String size = doc.get("size");
            System.out.println("name："+name);
            System.out.println("size："+size);
            System.out.println("=============");
        }

    }


    @After
    public void destroy() throws Exception{
        indexReader.close();
    }

}
