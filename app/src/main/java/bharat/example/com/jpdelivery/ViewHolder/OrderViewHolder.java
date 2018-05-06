package bharat.example.com.jpdelivery.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import bharat.example.com.jpdelivery.Interface.ItemClickListener;
import bharat.example.com.jpdelivery.R;


/**
 * Created by bharat on 2/28/18.
 */

public class OrderViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener,
       // View.OnLongClickListener,
        View.OnCreateContextMenuListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone,txtOrderAddress,txtOrderdate,orderName;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderdate = (TextView)itemView.findViewById(R.id.order_date);
        orderName = (TextView)itemView.findViewById(R.id.order_name);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        //itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
            }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
            }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select an Action");

            menu.add(0, 0, getAdapterPosition(), "Delivered");
            menu.add(0, 1, getAdapterPosition(), "Deliverey attempted");
            menu.add(0,2,getAdapterPosition(),"Delivery Failed");

    }

   /* @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }*/
}
