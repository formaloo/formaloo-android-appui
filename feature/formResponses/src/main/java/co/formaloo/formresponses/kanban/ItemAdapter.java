package co.formaloo.formresponses.kanban;

import static co.formaloo.common.MehodsKt.loadImage;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import co.formaloo.common.Constants;
import co.formaloo.formresponses.R;
import co.formaloo.model.form.Fields;
import co.formaloo.model.submit.RenderedData;
import co.formaloo.model.submit.Row;
import timber.log.Timber;

class ItemAdapter extends DragItemAdapter<Pair<Long, Row>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private Fields featuredImageField;
    private Fields itemsField;
    private ColumnItemListener listener;

    ItemAdapter(ArrayList<Pair<Long, Row>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Fields _featuredImageField, Fields _itemsField, ColumnItemListener _listener) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        itemsField = _itemsField;
        featuredImageField = _featuredImageField;
        listener = _listener;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Pair<Long, Row> longStringPair = mItemList.get(position);
        Timber.e("onBindViewHolder " + longStringPair);

        Row row = longStringPair.second;
        Map<String, RenderedData> rendered_data = row.getRendered_data();
        String slug = itemsField.getSlug();
        String featuredImageFieldSlug = featuredImageField.getSlug();

        String text = "";

        if (rendered_data != null && rendered_data.containsKey(slug)) {
            RenderedData renderedData = rendered_data.get(slug);
            text = Objects.requireNonNull(Objects.requireNonNull(renderedData).getValue()).toString();
            holder.mText.setText(text);

        } else {
            text = "";

        }

        if (rendered_data != null && rendered_data.containsKey(featuredImageFieldSlug)) {
            RenderedData renderedData = rendered_data.get(featuredImageFieldSlug);
            Object imgUrl = Objects.requireNonNull(renderedData).getValue();
            if (imgUrl != null && imgUrl.toString().contains(Constants.HREF)) {
                var v = imgUrl.toString();

                v = v.substring(v.indexOf("\"") + 1);
                v = v.substring(0, v.indexOf("\""));

                loadImage(v, holder.mImg);

            } else {
                holder.mImg.setVisibility(View.GONE);
            }


        } else {
            holder.mImg.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                listener.openContent(row);

            }
        });
        holder.mText.setText(text);

        holder.itemView.setTag(longStringPair);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;
        ShapeableImageView mImg;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
            mImg = (ShapeableImageView) itemView.findViewById(R.id.image);
        }

    }
}
