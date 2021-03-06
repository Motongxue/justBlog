package com.nbclass.service.impl;

import com.github.pagehelper.PageHelper;
import com.nbclass.enums.SliderType;
import com.nbclass.framework.annotation.RedisCache;
import com.nbclass.mapper.CategoryMapper;
import com.nbclass.mapper.SliderMapper;
import com.nbclass.model.BlogCategory;
import com.nbclass.model.BlogSlider;
import com.nbclass.service.CategoryService;
import com.nbclass.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SliderServiceImpl
 *
 * @author nbclass
 * @version V1.0
 * @date 2019-10-18
 */
@Service
public class SliderServiceImpl implements SliderService {

    @Autowired
    private SliderMapper sliderMapper;

    @Override
    @RedisCache(key = "SLIDERS")
    public List<BlogSlider> selectAll() {
        PageHelper.orderBy("id desc");
        return sliderMapper.selectAll();
    }

    @Override
    public List<BlogSlider> selectList(Integer type, String name) {
        return sliderMapper.selectList(type,name);
    }

    @Override
    @RedisCache(key = "SLIDERS",flush = true)
    public void save(BlogSlider slider) {
        Date date = new Date();
        slider.setUpdateTime(date);
        if(slider.getId()==null){
            slider.setCreateTime(date);
            sliderMapper.insertSelective(slider);
        }else{
            sliderMapper.updateByPrimaryKeySelective(slider);
        }
    }

    @Override
    @RedisCache(key = "SLIDERS",flush = true)
    public void deleteBatch(Integer[] ids) {
        sliderMapper.deleteBatch(ids);
    }

}
