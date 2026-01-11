package com.example.quickcommercedemo.adapters;

import com.example.quickcommercedemo.models.OrderFormItem;
import java.util.List;

public class OrderFormAdapter extends MultiOrderFormAdapter {
    // Standardizing the name as per your required structure
    public OrderFormAdapter(List<OrderFormItem> items) {
        super(items);
    }
}
