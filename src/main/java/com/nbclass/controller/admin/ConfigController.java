package com.nbclass.controller.admin;

import com.nbclass.enums.ConfigKey;
import com.nbclass.framework.annotation.AccessToken;
import com.nbclass.framework.util.CoreConst;
import com.nbclass.framework.util.ResponseUtil;
import com.nbclass.service.ConfigService;
import com.nbclass.service.ThymeleafService;
import com.nbclass.vo.ConfigEmailVo;
import com.nbclass.vo.ConfigStorageVo;
import com.nbclass.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/config")
public class ConfigController {
    @Autowired
    private ConfigService configService;
    @Autowired
    private ThymeleafService thymeleafService;

    @PostMapping("/save")
    @AccessToken
    public ResponseVo save(@RequestParam Map<String,String> map){
        try {
            for (String key : map.keySet()) {
                configService.updateByKey(key,map.get(key));
            }
            configService.updateByKey(ConfigKey.SYSTEM_IS_SET.getValue(), CoreConst.STATUS_VALID_STRING);
            thymeleafService.initConfig();
            return ResponseUtil.success("设置保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.error("设置保存失败");
        }
    }

    @PostMapping(value = "/save/storage")
    @AccessToken
    public ResponseVo saveConfig(ConfigStorageVo config){
        config.setSetFlag(CoreConst.STATUS_VALID);
        configService.saveStorageConfig(config);
        configService.updateByKey(ConfigKey.SYSTEM_IS_SET.getValue(), CoreConst.STATUS_VALID_STRING);
        return ResponseUtil.success("存储设置成功！");
    }

    @PostMapping(value = "/save/email")
    @AccessToken
    public ResponseVo saveConfig(ConfigEmailVo config){
        config.setSetFlag(CoreConst.STATUS_VALID);
        configService.saveEmailConfig(config);
        configService.updateByKey(ConfigKey.SYSTEM_IS_SET.getValue(), CoreConst.STATUS_VALID_STRING);
        return ResponseUtil.success("邮件设置成功！");
    }

}
