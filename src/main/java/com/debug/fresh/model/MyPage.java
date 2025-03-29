package com.debug.fresh.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPage {
    private long current; //当前第几页
    private long pages; //一共多少页
    private long size; //每页显示数
    private long total; //总数据

    public MyPage(IPage page) {
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.size = page.getSize();
        this.total = page.getTotal();
    }

}
