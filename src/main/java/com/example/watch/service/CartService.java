package com.example.watch.service;

import com.example.watch.entity.*;
import com.example.watch.dto.*;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service @Transactional
public class CartService {
    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo,
                       UserRepository userRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo; this.itemRepo = itemRepo;
        this.userRepo = userRepo; this.productRepo = productRepo;
    }

    public CartDTO getCart(Long userId) {
        Cart cart = cartRepo.findByUserUserId(userId);
        if (cart == null) {
            User u = userRepo.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException("User not found"));
            cart = new Cart(); cart.setUser(u);
            cart = cartRepo.save(cart);
        }
        CartDTO dto = new CartDTO(); dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser().getUserId());
        dto.setItems(cart.getItems().stream().map(item -> {
            CartItemDTO cd = new CartItemDTO();
            cd.setItemId(item.getItemId());
            cd.setProductId(item.getProduct().getProductId());
            cd.setQuantity(item.getQuantity());
            return cd;
        }).collect(Collectors.toList()));
        return dto;
    }

    public CartItemDTO addItem(Long userId, CartItemDTO dto) {
        CartDTO cart = getCart(userId);
        CartItem item = new CartItem();
        item.setCart(cartRepo.getById(cart.getCartId()));
        Product p = productRepo.findById(dto.getProductId()).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
        item.setProduct(p); item.setQuantity(dto.getQuantity());
        item = itemRepo.save(item);
        dto.setItemId(item.getItemId()); return dto;
    }

    public void removeItem(Long itemId) { itemRepo.deleteById(itemId); }

    public void clearCart(Long userId) {
        CartDTO cart = getCart(userId);
        cart.getItems().forEach(i -> itemRepo.deleteById(i.getItemId()));
    }
}