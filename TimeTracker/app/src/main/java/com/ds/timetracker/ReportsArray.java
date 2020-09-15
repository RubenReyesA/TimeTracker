package com.ds.timetracker;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportsArray implements Serializable {
    private final ArrayList<ReportsDetails> reportsDetailsArrayList;

    public ReportsArray(){
        this.reportsDetailsArrayList = new ArrayList<>();
    }

    public ArrayList<ReportsDetails> getReportsArray() {
        return this.reportsDetailsArrayList;
    }

    public void addReport(final String name, final String path, final String type){

        this.reportsDetailsArrayList.add(new ReportsDetails(name,path,type));
    }

    public void deleteReport(final int id){

        boolean found = false;
        int index = 0;

        while(index< this.reportsDetailsArrayList.size() && !found){
            if(this.reportsDetailsArrayList.get(index).getId()==id){
                found=true;
            }
            else{
                index++;
            }
        }

        if(found){
            this.reportsDetailsArrayList.remove(index);
        }

    }

    public int getSize(){
        return this.reportsDetailsArrayList.size();
    }
}
