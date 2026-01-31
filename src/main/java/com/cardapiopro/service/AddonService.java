package com.cardapiopro.service;

import com.cardapiopro.entity.Addon;
import com.cardapiopro.entity.AddonCategory;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.AddonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddonService {

    private final AddonRepository addonRepository;
    private final AddonCategoryService addonCategoryService;

    public List<Addon> findAll() {
        return addonRepository.findByActiveTrueAndAvailableTrueOrderByDisplayOrderAsc();
    }

    public Addon findById(UUID id) {
        return addonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adicional não encontrado: " + id));
    }

    public List<Addon> findByCategoryId(UUID categoryId) {
        return addonRepository.findByAddonCategoryIdAndActiveTrueOrderByDisplayOrderAsc(categoryId);
    }

    @Transactional
    public Addon create(Addon addon, UUID categoryId) {
        if (addonRepository.existsByName(addon.getName())) {
            throw new IllegalArgumentException("Já existe um adicional com este nome: " + addon.getName());
        }

        AddonCategory category = addonCategoryService.findById(categoryId);
        addon.setAddonCategory(category);

        return addonRepository.save(addon);
    }

    @Transactional
    public Addon update(UUID id, Addon updatedData) {
        Addon addon = findById(id);

        if (updatedData.getName() != null) {
            addon.setName(updatedData.getName());
        }
        if (updatedData.getDescription() != null) {
            addon.setDescription(updatedData.getDescription());
        }
        if (updatedData.getPrice() != null) {
            addon.setPrice(updatedData.getPrice());
        }
        if (updatedData.getMaxQuantity() != null) {
            addon.setMaxQuantity(updatedData.getMaxQuantity());
        }
        if (updatedData.getDisplayOrder() != null) {
            addon.setDisplayOrder(updatedData.getDisplayOrder());
        }

        return addonRepository.save(addon);
    }

    @Transactional
    public void toggleAvailability(UUID id) {
        Addon addon = findById(id);
        addon.setAvailable(!addon.isAvailable());
        addonRepository.save(addon);
    }

    @Transactional
    public void delete(UUID id) {
        Addon addon = findById(id);
        addon.setActive(false);
        addonRepository.save(addon);
    }
}