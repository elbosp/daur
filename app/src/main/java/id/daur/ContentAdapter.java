package id.daur;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import id.daur.model.FoodComposition;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private ArrayList<FoodComposition> dataList;
    private Context context;


    public ContentAdapter(ArrayList<FoodComposition> dataList, Context context_) {
        this.dataList = dataList;
        this.context = context_;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.content_recycler_view, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentViewHolder holder, int position) {
        final int pos = position;
        String totalKal = ""+dataList.get(position).getTotal_kal()+" Kkal";
        String menu = dataList.get(position).getNama_makanan();
        String portion = dataList.get(position).getKet_makanan();
        String namaMenu = dataList.get(position).getNama_menu();

        String detailMenu = menuDetail(menu, portion);

        holder.txtName.setText(detailMenu);
        holder.txtInfo.setText(totalKal);
        holder.txtNamaMenu.setText(namaMenu);
        Glide.with(this.context)
                .load(dataList.get(position).getGambar_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgContent);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName, txtInfo, txtNamaMenu;
        private ImageView imgContent;

        public ContentViewHolder(View itemView) {
            super(itemView);

            imgContent = itemView.findViewById(R.id.img_content);
            txtName = itemView.findViewById(R.id.txt_name);
            txtInfo = itemView.findViewById(R.id.txt_info);
            txtNamaMenu = itemView.findViewById(R.id.txt_title);
        }
    }

    private String menuDetail(String rawMenu, String rawPortion) {
        String[] menuDetail = rawMenu.split("_", 5);
        String[] portionDetail = rawPortion.split("_",5);

        String result = "";

        for(int i=0; i<menuDetail.length; i++) {
            String lastString="";
            if (i<(menuDetail.length)-1) {
                lastString="\n";
            }
            result = result + portionDetail[i]+" "+menuDetail[i]+lastString;
        }
        return result;
    }
}
