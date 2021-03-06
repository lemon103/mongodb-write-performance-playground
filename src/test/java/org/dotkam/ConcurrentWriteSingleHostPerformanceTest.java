package org.dotkam;

import com.mongodb.*;
import org.dotkam.mongodb.concurrent.MongoDocumentWriter;
import org.dotkam.mongodb.concurrent.MongoSingleHostDocumentWriter;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.GridSizeDocumentPartitioner;
import org.dotkam.record.VeryImportantRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ConcurrentWriteSingleHostPerformanceTest {

    private static final int HU_MONGO_US_NUMBER_OF_RECORDS = 100000;
    private static final int GRID_SIZE = 3;

    private static final String DB_NAME = "writePerformanceDumbDb";
    private static final String COLLECTION_NAME = "vipRecords";

    // to make sure every _test run_ produces (to a degree) unique documents
    private static final long uniqueSalt = new Date().getTime();

    private CollectionDataSource dataSource;
    private MongoDocumentWriter documentWriter;

    @Before
    public void createDbAndCollection() throws Exception {

        dataSource = new CollectionDataSource( DB_NAME, COLLECTION_NAME );

        dataSource.getCollection().drop();

        Serializable vir = createVeryImportantRecord( 0 );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(vir);
        oos.close();
        System.out.println( baos.size() );

        documentWriter = new MongoSingleHostDocumentWriter(  VeryImportantRecord.class,
                                                   new GridSizeDocumentPartitioner(),
                                                   dataSource,
                                                   GRID_SIZE );
    }

    @After
    public void dropDataBase() throws Exception {
        //dataSource.getCollection().drop();
    }

    @Test
    public void insertRecordsWithPartitioning() throws Exception {

        StopWatch timer = new StopWatch("-- MongoDB Insert All With Partitioning [ grid size = " + GRID_SIZE + " ] --");

        timer.start( "adding " + HU_MONGO_US_NUMBER_OF_RECORDS + " number of documents.." );

        List<DBObject> documents = new ArrayList<DBObject>();

        for ( long i = 0; i < HU_MONGO_US_NUMBER_OF_RECORDS; i++ ) {
            documents.add( createVeryImportantRecord( i ) );
        }

        documentWriter.write( documents );

        timer.stop();

//        // giving "some" time for documents to propagate to disk
//        Thread.sleep( 10000 );
//
//        DBCollection collection = ( new Mongo() ).getDB( DB_NAME ).getCollection( COLLECTION_NAME );
//        assertEquals ( "database has unexpected number of records",
//                       HU_MONGO_US_NUMBER_OF_RECORDS, collection.count() );

        System.out.println( timer.prettyPrint() );
    }

    private VeryImportantRecord createVeryImportantRecord( long id ) {

        VeryImportantRecord viRecord = new VeryImportantRecord();

        viRecord.setYou( "you" );
        viRecord.setCant( "cant" );
        viRecord.setLose( "lose" );
        viRecord.setMe( "me" );
        viRecord.setBecause( "because" );
        viRecord.setI( "I" );
        viRecord.setAm( "am" );
        viRecord.setAn( "an" );
        viRecord.setExtremely( "extremely" );
        viRecord.setImportant( "important" );
        viRecord.setRecord( "record" );
        viRecord.setWith( "with" );
        viRecord.setBusinessId( id );
        viRecord.setIwould( "I would" );
        viRecord.setLike( "like" );
        viRecord.setTo( "to" );
        viRecord.setBe( "be" );
        viRecord.setPersisted( "persisted" );
        viRecord.setAnd( "and" );
        viRecord.setKept( "kept" );
        viRecord.setIn( "in" );
        viRecord.setDry( "dry" );
        viRecord.setCozy( "cozy" );
        viRecord.setPlace( "place" );
        viRecord.setAlso( "also" );
        viRecord.setBring( "bring" );
        viRecord.setMyself( "my self" );
        viRecord.setSome( "some" );
        viRecord.setSalty( uniqueSalt );
        viRecord.setMongos( "mongos" );

        return viRecord;
    }
}
