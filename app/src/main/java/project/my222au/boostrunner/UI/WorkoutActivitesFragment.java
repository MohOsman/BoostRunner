package project.my222au.boostrunner.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.model.database.WorkoutData;
import project.my222au.boostrunner.model.database.WorkoutDataBase;

public class WorkoutActivitesFragment extends Fragment {
    RecyclerView mRecyclerView;
    WorkoutDataBase mWorkoutDataBase;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mWorkoutDataBase = new WorkoutDataBase(getActivity());
        try {
            mWorkoutDataBase.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
     List<WorkoutData> list = mWorkoutDataBase.getAllWorkoutdata();
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view,container,false);
        RecyclerViewAdapter  adapter  = new RecyclerViewAdapter(list);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mRecyclerView;
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView mduration;
        public  TextView mDistance;
        public  TextView mCaloris;

        public ViewHolder(LayoutInflater inflator, ViewGroup parent) {
           super(inflator.inflate(R.layout.list_item_layout,parent,false)) ;
            mduration = (TextView) itemView.findViewById(R.id.date_label_list);
            mDistance = (TextView)itemView.findViewById(R.id.distance_list_item);
            mCaloris =(TextView) itemView.findViewById(R.id.time_list_item);

        }
    }





    public static  class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<WorkoutData> list;

        public RecyclerViewAdapter(List<WorkoutData> data) {
            list = data;



        }

        @Override

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return  new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mduration.setText(list.get(position).getDuration());
            holder.mDistance.setText(list.get(position).getDistance());
            holder.mCaloris.setText(list.get(position).getCaloris());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
