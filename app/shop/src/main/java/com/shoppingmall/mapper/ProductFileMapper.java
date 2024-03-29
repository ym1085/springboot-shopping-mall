package com.shoppingmall.mapper;

import com.shoppingmall.dto.request.FileSaveRequestDto;
import com.shoppingmall.vo.ProductFiles;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductFileMapper {

    int saveFiles(List<FileSaveRequestDto> files);

    List<ProductFiles> getFilesByProductId(Integer productId);

    int countProductFileByProductId(Integer productId);

    int deleteFilesByProductId(Integer productId);
}