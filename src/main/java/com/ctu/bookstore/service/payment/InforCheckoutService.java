package com.ctu.bookstore.service.payment;

import com.ctu.bookstore.dto.request.payment.InforCheckoutRequestUpdate;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.identity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InforCheckoutService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    public InforCheckout getMyInforCheckout(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        return user.get().getInforCheckout();
    }
    public InforCheckout updateInfo(InforCheckoutRequestUpdate requestUpdate){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        InforCheckout info = user.get().getInforCheckout();
        if (requestUpdate.getName() != null || requestUpdate.getName() != ""){
            info.setName(requestUpdate.getName());
        }
        if (requestUpdate.getPhoneNumber() != null && requestUpdate.getPhoneNumber() != ""){
            info.setPhoneNumber(requestUpdate.getPhoneNumber());
        }
        if (requestUpdate.getEmail() != null && requestUpdate.getEmail() != ""){
            info.setEmail(requestUpdate.getEmail());
        }
        if (requestUpdate.getAdress() != null || requestUpdate.getAdress() != ""){
            info.setAdress(requestUpdate.getAdress());
        }
        if (requestUpdate.getNote() != null || requestUpdate.getNote() != ""){
            info.setNote(requestUpdate.getNote());
        }
        if (requestUpdate.getVoucher() != null || requestUpdate.getVoucher() != ""){
            info.setVoucher(requestUpdate.getVoucher());
        }
        userService.updateInforCheckout(info);
        return info;
    }
}
