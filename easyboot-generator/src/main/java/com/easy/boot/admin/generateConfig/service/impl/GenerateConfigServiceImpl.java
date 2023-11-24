package com.easy.boot.admin.generateConfig.service.impl;

import cn.hutool.core.text.NamingCase;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.boot.admin.generate.entity.DatabaseTable;
import com.easy.boot.admin.generate.service.GenerateService;
import com.easy.boot.admin.generateConfig.entity.*;
import com.easy.boot.admin.generateConfig.mapper.GenerateConfigMapper;
import com.easy.boot.admin.generateConfig.service.IGenerateConfigService;
import com.easy.boot.generator.db.DbManager;
import com.easy.boot.core.redisson.EasyLock;
import com.easy.boot.core.exception.GeneratorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zoe
* @date 2023/09/10
* @description 代码生成参数配置 服务实现类
*/
@Service
public class GenerateConfigServiceImpl extends ServiceImpl<GenerateConfigMapper, GenerateConfig> implements IGenerateConfigService {


    @EasyLock
    @Override
    public GenerateConfigVO getGlobalConfig() {
        GenerateConfig generateConfig = lambdaQuery()
                .eq(GenerateConfig::getType, 1)
                .one();
        if (generateConfig == null) {
            generateConfig = GenerateConfig.defaultGlobalBuild();
            save(generateConfig);
        }
        GenerateConfigVO vo = GenerateConfigVO.toGenerateConfigVO(generateConfig);
        return vo;
    }

    @EasyLock
    @Override
    public GenerateConfigVO getTableConfig(GenerateConfigQuery query) {
        GenerateService generateService = SpringUtil.getBean(GenerateService.class);
        DatabaseTable databaseTable = generateService.getTableByTableName(query.getTableName());
        if (databaseTable == null) {
            throw new GeneratorException("当前表配置信息不存在");
        }
        GenerateConfig generateConfig = lambdaQuery()
                .eq(GenerateConfig::getType, 2)
                .eq(GenerateConfig::getTableName, query.getTableName())
                .one();
        GenerateConfigVO vo = GenerateConfigVO.toGenerateConfigVO(generateConfig);
        if (generateConfig == null) {
            vo = getGlobalConfig();
            String filterName = DbManager.filterTableName(databaseTable.getTableName(),
                    vo.getExcludeTablePrefix(), vo.getExcludeTableSuffix());
            vo.setType(2)
                    .setTableName(databaseTable.getTableName())
                    .setModuleName(NamingCase.toCamelCase(filterName))
                    .setTableRemarks(databaseTable.getComment());
        }
        return vo;
    }

    @EasyLock
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateGlobalConfig(GenerateConfigGlobalUpdateDTO dto) {
        GenerateConfig generateConfig = GenerateConfigGlobalUpdateDTO.toGenerateConfig(dto);
        generateConfig.setType(1)
                .setTableName("")
                .setTableRemarks("");
        deleteGlobal();
        return save(generateConfig);
    }

    @Override
    public Boolean updateByTableName(GenerateConfigUpdateDTO dto) {
        GenerateConfig generateConfig = GenerateConfigUpdateDTO.toGenerateConfig(dto);
        deleteByTableName(generateConfig.getTableName());
        return save(generateConfig);
    }

    @Override
    public Boolean deleteGlobal() {
        QueryWrapper<GenerateConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 1);
        return remove(queryWrapper);
    }

    @Override
    public Boolean deleteByTableName(String tableName) {
        QueryWrapper<GenerateConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_name", tableName);
        return remove(queryWrapper);
    }

    @Override
    public Boolean deleteBatchByTableNames(List<String> tableNames) {
        QueryWrapper<GenerateConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("table_name", tableNames);
        return remove(queryWrapper);
    }

}