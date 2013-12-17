package com.jc.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by amclaughlin on 12/15/13.
 */
public class DBHandler {
    private DBModel model;
    private SQLiteDatabase database;

    private String [] allColumns = {
            DBModel.TASK_NAME,
            DBModel.COMPLETE,
            DBModel.TIME,
            DBModel.LENGTH,
            DBModel.WHEN,
            DBModel.TASK_ID,
    };

    public DBHandler(Context context) {
        model = new DBModel(context);
    }

    //Opening the Database (Getting the writable Database)
    public void open(){
        database = model.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    //Update a post
    public void updateTask(Task task){
        deleteTaskById(task.id);
        addTask(task);
    }

    public ArrayList<String> getAllTaskIds() {
        Cursor cursor = database.query(DBModel.TABLE_NAME, new String[]{DBModel.TASK_ID},null,null,null,null,null);
        ArrayList<String> ids = new ArrayList<String>();
        cursor.moveToFirst();
        String CurID;
        while (!cursor.isAfterLast()){
            CurID = cursor.getString(0);
            if (CurID.length() > 16) {
                ids.add(CurID);
            }
            cursor.moveToNext();
        }
        cursor.close();
        if (ids.size() < 1) {
            ids.add("aaa");
        }
        Log.i("ids", ids.toString());
        return ids;
    }

    public void addTask(Task newTask) {
        ContentValues values = new ContentValues();

        values.put(DBModel.TASK_NAME, newTask.name);
        values.put(DBModel.COMPLETE, newTask.complete);
        values.put(DBModel.LENGTH, newTask.length);
        values.put(DBModel.TIME, newTask.time);
        values.put(DBModel.WHEN, newTask.when);

        this.database.insert(DBModel.TABLE_NAME,null,values);
    }

    //Getting Tasks by Size in descending priority order
    public ArrayList<Task> getTasks(){
        return sweepCursor(
                database.query(DBModel.TABLE_NAME, allColumns, null, null, null, null,DBModel.WHEN+ " DESC"));
        }

        //Delete Tasks by ID
        public void deleteTaskById(String id){
            database.delete(DBModel.TABLE_NAME, DBModel.TASK_ID + " like '%" + id + "%'", null);
        }

    //Get Tasks from Cursor
    public ArrayList<Task> sweepCursor (Cursor cursor) {
        ArrayList<Task> tasks = new ArrayList<Task>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            tasks.add(cursorToPost(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    //Get Tasks from Cursor
    public Task cursorToPost(Cursor cursor){
        Task task = new Task(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4)
        );

        task.setId(cursor.getString(5));
        Log.i ("id", cursor.getString(5));
        return task;
    }
}
