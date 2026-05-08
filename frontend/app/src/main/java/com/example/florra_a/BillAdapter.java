package com.example.florra_a;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.models.Bill;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    private Context context;
    private List<Bill> billList;

    public BillAdapter(Context context, List<Bill> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = billList.get(position);

        holder.tvBillNo.setText("Bill #" + bill.getBillNo());
        holder.tvCustomerName.setText(bill.getCustomerName());
        holder.tvAmount.setText("₹" + (int) bill.getGrandTotal());
        
        // Simple date formatting (Backend returns ISO string, ideally parse it)
        String date = bill.getCreatedAt(); 
        if(date != null && date.length() > 10) {
            date = date.substring(0, 10); // Show only YYYY-MM-DD
        }
        holder.tvDate.setText(date);

        // Status Styling
        holder.tvBillStatus.setText(bill.getStatus());
        if ("Paid".equalsIgnoreCase(bill.getStatus())) {
            holder.tvBillStatus.setBackgroundResource(R.drawable.bg_stock_in); // Reusing green bg
            holder.tvBillStatus.setTextColor(context.getResources().getColor(R.color.emerald_700));
        } else {
            holder.tvBillStatus.setBackgroundResource(R.drawable.bg_stock_low); // Reusing amber bg
            holder.tvBillStatus.setTextColor(context.getResources().getColor(R.color.amber_700));
        }

        // Actions
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewBillActivity.class);
            intent.putExtra("bill", bill);
            context.startActivity(intent);
        });



        holder.btnShare.setOnClickListener(v -> {
            shareBillAsPdf(bill);
        });
    }

    private void shareBillAsPdf(Bill bill) {
        // 1. Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.activity_view_bill, null);
        
        // 2. Find and Populate Views (Same logic as ViewBillActivity)
        TextView tvBillNumber = view.findViewById(R.id.tvBillNumber);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvStatus = view.findViewById(R.id.tvStatus);
        TextView tvCustomerName = view.findViewById(R.id.tvCustomerName);
        TextView tvMobile = view.findViewById(R.id.tvMobile);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvSubtotal = view.findViewById(R.id.tvSubtotal);
        TextView tvGST = view.findViewById(R.id.tvGST);
        TextView tvDiscount = view.findViewById(R.id.tvDiscount);
        TextView tvGrandTotal = view.findViewById(R.id.tvGrandTotal);
        android.widget.LinearLayout llItemsContainer = view.findViewById(R.id.llItemsContainer);

        // Populate Data
        tvBillNumber.setText(bill.getBillNo());
        tvDate.setText(bill.getCreatedAt());
        tvStatus.setText(bill.getStatus());
        tvCustomerName.setText(bill.getCustomerName());
        tvMobile.setText(bill.getCustomerPhone());
        tvAddress.setText(bill.getCustomerAddress());
        
        tvSubtotal.setText("₹" + (int)bill.getSubtotal());
        tvGST.setText("₹" + (int)bill.getGstAmount());
        tvDiscount.setText("-₹" + (int)bill.getDiscount());
        tvGrandTotal.setText("₹" + (int)bill.getGrandTotal());

        // Populate Items
        if (bill.getItems() != null && llItemsContainer != null) {
            llItemsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (com.example.florra_a.models.BillItem item : bill.getItems()) {
                View itemView = inflater.inflate(R.layout.item_preview_row, llItemsContainer, false);
                ((TextView) itemView.findViewById(R.id.tvItemName)).setText(item.getItemName());
                ((TextView) itemView.findViewById(R.id.tvItemDetails)).setText(item.getSize());
                ((TextView) itemView.findViewById(R.id.tvItemQty)).setText(String.valueOf(item.getQuantity())); 
                ((TextView) itemView.findViewById(R.id.tvItemRate)).setText("₹" + (int)item.getRate());
                ((TextView) itemView.findViewById(R.id.tvItemAmount)).setText("₹" + (int)item.getAmount());
                llItemsContainer.addView(itemView);
            }
        }

        // 3. Find the container to draw
        View billContainer = view.findViewById(R.id.billContainer);
        if (billContainer == null) {
            Toast.makeText(context, "Error generating PDF layout", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Measure and Layout
        android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        
        int measureWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        billContainer.measure(measureWidth, measureHeight);
        int totalHeight = billContainer.getMeasuredHeight();
        int totalWidth = billContainer.getMeasuredWidth();
        
        billContainer.layout(0, 0, totalWidth, totalHeight);

        // 5. Create PDF
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(totalWidth, totalHeight, 1).create();
        android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        canvas.drawColor(android.graphics.Color.WHITE);
        billContainer.draw(canvas);

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColor(android.graphics.Color.GRAY);
        paint.setTextSize(20);
        canvas.drawText("Generated by Florra Tiles App", 20, totalHeight - 20, paint);

        document.finishPage(page);

        // 6. Save and Share
        try {
            java.io.File cachePath = new java.io.File(context.getCacheDir(), "documents");
            cachePath.mkdirs();
            java.io.File newFile = new java.io.File(cachePath, "Bill_" + bill.getBillNo() + ".pdf");
            java.io.FileOutputStream stream = new java.io.FileOutputStream(newFile);
            document.writeTo(stream);
            document.close();
            stream.close();

            android.net.Uri contentUri = androidx.core.content.FileProvider.getUriForFile(context, context.getPackageName() + ".provider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, "application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                context.startActivity(Intent.createChooser(shareIntent, "Share Bill PDF"));
            }

        } catch (java.io.IOException e) {
            Toast.makeText(context, "Error sharing PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillNo, tvCustomerName, tvDate, tvAmount, tvBillStatus;
        ImageView btnShare, btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillNo = itemView.findViewById(R.id.tvBillNo);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBillStatus = itemView.findViewById(R.id.tvBillStatus);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
