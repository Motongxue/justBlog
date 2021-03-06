package com.nbclass.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nbclass.framework.annotation.AccessToken;
import com.nbclass.framework.util.ResponseUtil;
import com.nbclass.model.BlogLink;
import com.nbclass.service.LinkService;
import com.nbclass.vo.LinkVo;
import com.nbclass.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/link")
public class LinkController {
    @Autowired
    private LinkService linkService;

    @PostMapping("/list")
    @AccessToken
    public ResponseVo loadLinks(LinkVo vo){
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        List<BlogLink> linkList = linkService.selectList(vo);
        PageInfo<BlogLink> pageInfo = new PageInfo<>(linkList);
        return ResponseUtil.success(pageInfo);
    }

    @PostMapping("/save")
    @AccessToken
    public ResponseVo add(BlogLink link){
        linkService.save(link);
        return ResponseUtil.success("保存友链成功");
    }

    @PostMapping("/delete")
    @AccessToken
    public ResponseVo delete(@RequestParam("ids[]") Integer[]ids){
        linkService.deleteBatch(ids);
        return ResponseUtil.success("删除友链成功");
    }

}
