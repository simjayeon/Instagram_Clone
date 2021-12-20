package com.example.instagram_clone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.ContentDTO;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GridFragment extends Fragment {
    View fragmentView;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_grid, container, false);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = fragmentView.findViewById(R.id.gridfragment_recyclerview);

        //Adapter 연결해주는 역할
        recyclerView.setAdapter(new GridFragmentAdapter()); //어댑터 설정
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return fragmentView;
    }


    public class GridFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<ContentDTO> contentDTOS; //data transfer object 프로세스 간의 데이터를 전달하는 객체

        public GridFragmentAdapter(){
            contentDTOS = new ArrayList<>();
            //파이어스토어에서 데이터 값 읽어오기
            firestore.collection("images").addSnapshotListener((value, error) -> {
                if(value == null){
                    //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                }
                //데이터를 받아주는 부분
                for(QueryDocumentSnapshot doc : value){
                    contentDTOS.add(doc.toObject(ContentDTO.class));
                }
                notifyDataSetChanged(); //새로고침
            });
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //화면의 폭을 가져옴
            int width = parent.getResources().getDisplayMetrics().widthPixels / 3;

            //폭의 값을 이미지뷰에 넣음 (정사각형 이미지)
            //getContext는 추상클래스, 애플리케이션의 현재 상태
            ImageView imageView = new ImageView(parent.getContext());
            //3*3
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));

            return new CustomViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            Glide.with(holder.itemView.getContext())
                    .load(contentDTOS.get(position).imageUrl)
                    .centerCrop()
                    .into(((CustomViewHolder) holder).imageView);
        }

        @Override
        public int getItemCount() {
            return contentDTOS.size();
        }


        //한번 생성해두면 객체 로딩 시간이 줄어듬
        //메모리 낭비할 필요 없이, 객체를 로딩 시간이 줄어듬
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public CustomViewHolder(ImageView imageView) {
                super(imageView);
                this.imageView = imageView;

            }
        }
    }
}
