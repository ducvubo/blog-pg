package com.blog.blog_pg.service.impl;

import com.blog.blog_pg.dto.request.label.CreateLabelDto;
import com.blog.blog_pg.dto.request.label.UpdateLabelDto;
import com.blog.blog_pg.dto.request.label.UpdateStatusLabelDto;
import com.blog.blog_pg.dto.response.MetaPagination;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.entities.LabelEntity;
import com.blog.blog_pg.enums.EnumStatus;
import com.blog.blog_pg.exception.BadRequestError;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.repository.LabelRepository;
import com.blog.blog_pg.service.LabelService;
import com.blog.blog_pg.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public LabelEntity createLabel(CreateLabelDto createLabelDto, Account account) {
        try{
            LabelEntity labelEntity = LabelEntity.builder()
                    .lb_name(createLabelDto.getLb_name())
                    .lb_description(createLabelDto.getLb_description())
                    .lb_color(createLabelDto.getLb_color())
                    .lb_res_id(account.getAccountRestaurantId())
                    .lb_status(EnumStatus.ENABLED)
                    .createdBy(AccountUtils.convertAccountToJson(account))
                    .updatedBy(AccountUtils.convertAccountToJson(account))
                    .isDeleted(0)
                    .build();

            labelRepository.save(labelEntity);
            return labelEntity;
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public LabelEntity getLabel(String id) {
        try {
            UUID lb_id = UUID.fromString(id);
            Optional<LabelEntity> labelEntity = labelRepository.findById(lb_id);
            if (labelEntity.isEmpty()) {
                throw new BadRequestError("Label not found");
            }
            return labelEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LabelEntity updateLabel(UpdateLabelDto updateLabelDto, Account account) {
        try {
            UUID id = UUID.fromString(updateLabelDto.getLb_id());

            // Update label
            Optional<LabelEntity> labelEntity = labelRepository.findById(id);
            if (labelEntity.isEmpty()) {
                throw new BadRequestError("Label not found");
            }
            labelEntity.get().setLb_name(updateLabelDto.getLb_name());
            labelEntity.get().setLb_description(updateLabelDto.getLb_description());
            labelEntity.get().setLb_color(updateLabelDto.getLb_color());
            labelEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            labelRepository.save(labelEntity.get());

            return labelEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LabelEntity deleteLabel(String id, Account account) {
        try{
            UUID labelId = UUID.fromString(id);
            Optional<LabelEntity> labelEntity = labelRepository.findById(labelId);
            if (labelEntity.isEmpty()) {
                throw new BadRequestError("Label not found");
            }
            //update isDeleted = 1
            labelEntity.get().setIsDeleted(1);
            labelEntity.get().setDeletedBy(AccountUtils.convertAccountToJson(account));
            labelEntity.get().setDeletedAt(new Date(System.currentTimeMillis()));
            labelRepository.save(labelEntity.get());
            return labelEntity.get();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public LabelEntity restoreLabel(String id, Account account) {
        try {
            UUID lb_id = UUID.fromString(id);
            Optional<LabelEntity> labelEntity = labelRepository.findById(lb_id);
            if (labelEntity.isEmpty()) {
                throw new BadRequestError("Label not found");
            }
            labelEntity.get().setIsDeleted(0);
            labelEntity.get().setDeletedBy(null);
            labelEntity.get().setDeletedAt(null);
            labelRepository.save(labelEntity.get());
            return labelEntity.get();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public LabelEntity updateStatus(UpdateStatusLabelDto updateStatusLabelDto, Account account) {
        try {
            UUID id = UUID.fromString(updateStatusLabelDto.getLb_id());

            // Update label
            Optional<LabelEntity> labelEntity = labelRepository.findById(id);
            if (labelEntity.isEmpty()) {
                throw new BadRequestError("Label not found");
            }
            labelEntity.get().setLb_status(updateStatusLabelDto.getLb_status());
            labelEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            labelRepository.save(labelEntity.get());

            return labelEntity.get();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResPagination<LabelEntity> getAllLabel(int pageIndex, int pageSize, String lb_name, Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<LabelEntity> labelEntities = labelRepository.findByFilters(lb_name, account.getAccountRestaurantId(), 0, pageable);

            return ResPagination.<LabelEntity>builder()
                    .result(labelEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(labelEntities.getTotalPages())
                            .totalItem(labelEntities.getTotalElements())
                            .build())
                    .build();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public ResPagination<LabelEntity> getAllLabelRecycleBin(int pageIndex, int pageSize, String lb_name, Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<LabelEntity> labelEntities = labelRepository.findByFilters(lb_name, account.getAccountRestaurantId(), 1, pageable);

            return ResPagination.<LabelEntity>builder()
                    .result(labelEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(labelEntities.getTotalPages())
                            .totalItem(labelEntities.getTotalElements())
                            .build())
                    .build();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<LabelEntity> getAllLabelByRestaurantId(Account account) {
        try {
            return labelRepository.findAllByRestaurantId(account.getAccountRestaurantId());
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }


}
