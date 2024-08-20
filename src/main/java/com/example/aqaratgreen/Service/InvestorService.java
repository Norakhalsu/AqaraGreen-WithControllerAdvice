package com.example.aqaratgreen.Service;

import com.example.aqaratgreen.Api.ApiException;
import com.example.aqaratgreen.Model.*;
import com.example.aqaratgreen.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InvestorService {

    private final InvestorRepository investorRepository;
    private final UnitRepository unitRepository;
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;

    public List<Investor> getAllInvestors() {
        return investorRepository.findAll();
    }

    public void addInvestor(Investor investor) {
        investorRepository.save(investor);
    }

    public void UpdateInvestor(Integer id ,Investor investor) {
        Investor inv = investorRepository.findInvestorById(id);

        if(investor == null) {
            throw new RuntimeException("Investor Not Found");
        }
        inv.setPhoneNumber(investor.getPhoneNumber());
        inv.setEmail(investor.getEmail());
        inv.setPassword(investor.getPassword());
        inv.setRole(investor.getRole());
        inv.setInvestor(investor.getInvestor());
        investorRepository.save(inv);
    }

    public void DeleteInvestor(Integer id) {
        Investor inv = investorRepository.findInvestorById(id);
        if(inv == null) {
            throw new RuntimeException("Investor Not Found");
        }
        investorRepository.delete(inv);
    }



 // -------------------------------- End Point --------------------------------------

    //حساب اجمالي الايرادات السنوي لمستثمر معين
    public double getTotalRevenueForInvestor(Integer investorId) {
        List<Unit> units = unitRepository.findUnitsByInvestorId(investorId);
        Investor inv = investorRepository.findInvestorById(investorId);
        if (units.isEmpty()) {
            throw new RuntimeException("No Unit found with id " + investorId);
        }
        double annualRevenue = 0;

        for (Unit unit : units) {
            if (unit.getOfferType().equals("rent")) {
                annualRevenue += unit.getPrice() * 12;
            }
            else if (unit.getOfferType().equals("buy")) {
                annualRevenue += unit.getPrice(); // Assuming buy price is for the whole year
            }
            else {
                throw new RuntimeException("Unit is not of type 'rent' or 'buy'");
             }
          }
        inv.setTotalRevenue(annualRevenue); // Update total revenue for the investor
        return annualRevenue;
    }

    //  *** الايجار المنتهي بالتملك : نقل الملكية
    public void endLeaseWithOwnership(Integer unitId, Integer investorId , Integer clientId) {
        Unit unit = unitRepository.findUnitById(unitId);
        Investor investor = investorRepository.findInvestorById(investorId);
        Client client=clientRepository.findClientById(clientId);

        if (unit == null) {
            throw new ApiException("*** الوحدة السكنية غير موجودة في النظام ***");
        }

        if (investor == null) {
            throw new ApiException("*** المستثمر غير موجود بالنظام****");
        }
        if (client==null) {
            throw new ApiException("**** العميل غير موجود بالنظام ****");
        }

        String unitOfferType = unit.getOfferType();
        if ("rent".equals(unitOfferType) || "buy".equals(unitOfferType)) {
            unit.setOfferType("Transfer-of-ownership");
            unit.setInvestorId(unit.getClientId());
        } else {
            throw new ApiException(" *** الوحدة موجودة بالفعل في حالة نقل الملكية ***");
        }
        unitRepository.save(unit);
    }


    // استرجاع قائمة بالعقود للمستثمر مصنفة الى المدفوعه و الغير مدفوعه
    public Map<String, List<Contract>> getContractsByPaymentStatus(Integer investorId) {
        List<Contract> allContracts =contractRepository.findContractsByInvestorId(investorId);
        List<Contract> paidContracts = new ArrayList<>();
        List<Contract> unpaidContracts = new ArrayList<>();

        for (Contract contract : allContracts) {
            if ("paid".equalsIgnoreCase(contract.getPayState())) {
                paidContracts.add(contract);
            } else {
                unpaidContracts.add(contract);
            }
        }
        Map<String, List<Contract>> contractsByPaymentStatus = new HashMap<>();
        contractsByPaymentStatus.put("العقود المدفوعة", paidContracts);
        contractsByPaymentStatus.put("العقود الغير مدفوعه", unpaidContracts);
        return contractsByPaymentStatus;
    }

}

