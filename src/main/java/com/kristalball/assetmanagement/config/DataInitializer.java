package com.kristalball.assetmanagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kristalball.assetmanagement.entity.Asset;
import com.kristalball.assetmanagement.entity.AssetCategory;
import com.kristalball.assetmanagement.entity.Base;
import com.kristalball.assetmanagement.entity.InventoryBalance;
import com.kristalball.assetmanagement.entity.Role;
import com.kristalball.assetmanagement.entity.User;
import com.kristalball.assetmanagement.repository.AssetCategoryRepository;
import com.kristalball.assetmanagement.repository.AssetRepository;
import com.kristalball.assetmanagement.repository.BaseRepository;
import com.kristalball.assetmanagement.repository.InventoryBalanceRepository;
import com.kristalball.assetmanagement.repository.RoleRepository;
import com.kristalball.assetmanagement.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BaseRepository baseRepository;
    private final AssetCategoryRepository categoryRepository;
    private final InventoryBalanceRepository inventoryRepository;
    private final AssetRepository assetRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            BaseRepository baseRepository,
            AssetCategoryRepository categoryRepository,
            InventoryBalanceRepository inventoryRepository,
            AssetRepository assetRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.baseRepository = baseRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.assetRepository = assetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role admin = getOrCreateRole("ADMIN");
        Role commander = getOrCreateRole("BASE_COMMANDER");
        Role logistics = getOrCreateRole("LOGISTICS_OFFICER");

        Base alpha = getOrCreateBase(
                "Alpha Base",
                "Bengaluru");

        Base bravo = getOrCreateBase(
                "Bravo Base",
                "Chennai");

        AssetCategory vehicles = getOrCreateCategory(
                "Vehicles",
                "Vehicles and transport equipment");

        AssetCategory weapons = getOrCreateCategory(
                "Weapons",
                "Weapons");

        AssetCategory ammunition = getOrCreateCategory(
                "Ammunition",
                "Ammunition");

        createUserIfMissing(
                "admin",
                "System Administrator",
                "admin123",
                admin,
                null);

        createUserIfMissing(
                "commander",
                "Alpha Base Commander",
                "commander123",
                commander,
                alpha);

        createUserIfMissing(
                "logistics",
                "Logistics Officer",
                "logistics123",
                logistics,
                null);

        createInventoryIfMissing(
                alpha, vehicles, 50);

        createInventoryIfMissing(
                alpha, weapons, 100);

        createInventoryIfMissing(
                alpha, ammunition, 500);

        createInventoryIfMissing(
                bravo, vehicles, 25);

        createInventoryIfMissing(
                bravo, weapons, 75);

        createInventoryIfMissing(
                bravo, ammunition, 250);

        createAssetIfMissing(
                "4x4 Military Vehicle",
                "Standard military transport vehicle",
                "AVAILABLE",
                "VEH-001",
                vehicles,
                alpha);

        createAssetIfMissing(
                "Service Rifle",
                "Standard service weapon",
                "AVAILABLE",
                "WPN-001",
                weapons,
                alpha);

        createAssetIfMissing(
                "5.56mm Ammunition",
                "5.56mm ammunition stock",
                "AVAILABLE",
                "AMMO-001",
                ammunition,
                alpha);

        createAssetIfMissing(
                "4x4 Military Vehicle",
                "Standard military transport vehicle",
                "AVAILABLE",
                "VEH-002",
                vehicles,
                bravo);

        createAssetIfMissing(
                "Service Rifle",
                "Standard service weapon",
                "AVAILABLE",
                "WPN-002",
                weapons,
                bravo);

        createAssetIfMissing(
                "5.56mm Ammunition",
                "5.56mm ammunition stock",
                "AVAILABLE",
                "AMMO-002",
                ammunition,
                bravo);
    }

    private Role getOrCreateRole(String name) {

        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }

    private Base getOrCreateBase(
            String name,
            String location) {

        return baseRepository.findAll()
                .stream()
                .filter(base -> name.equals(base.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Base base = new Base();
                    base.setName(name);
                    base.setLocation(location);
                    return baseRepository.save(base);
                });
    }

    private AssetCategory getOrCreateCategory(
            String name,
            String description) {

        return categoryRepository.findAll()
                .stream()
                .filter(category ->
                        name.equals(category.getName()))
                .findFirst()
                .orElseGet(() -> {

                    AssetCategory category =
                            new AssetCategory();

                    category.setName(name);
                    category.setDescription(description);

                    return categoryRepository.save(category);
                });
    }

    private void createUserIfMissing(
            String username,
            String fullName,
            String password,
            Role role,
            Base base) {

        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        /*
         * If the user already exists, update only the
         * Commander account so its password becomes
         * commander123 as configured above.
         */
        if (user != null) {

            if ("commander".equals(username)) {

                user.setFullName(fullName);
                user.setPassword(
                        passwordEncoder.encode(password));
                user.setRole(role);
                user.setBase(base);

                userRepository.save(user);
            }

            return;
        }

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setFullName(fullName);
        newUser.setPassword(
                passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setBase(base);

        userRepository.save(newUser);
    }

    private void createInventoryIfMissing(
            Base base,
            AssetCategory category,
            int openingBalance) {

        boolean exists = inventoryRepository.findAll()
                .stream()
                .anyMatch(item ->
                        item.getBase() != null
                                && item.getCategory() != null
                                && base.getId().equals(
                                        item.getBase().getId())
                                && category.getId().equals(
                                        item.getCategory().getId()));

        if (exists) {
            return;
        }

        InventoryBalance inventory =
                new InventoryBalance();

        inventory.setBase(base);
        inventory.setCategory(category);
        inventory.setOpeningBalance(openingBalance);

        inventoryRepository.save(inventory);
    }

    private void createAssetIfMissing(
            String name,
            String description,
            String status,
            String serialNumber,
            AssetCategory category,
            Base base) {

        boolean exists = assetRepository.findAll()
                .stream()
                .anyMatch(asset ->
                        serialNumber.equals(
                                asset.getSerialNumber()));

        if (exists) {
            return;
        }

        Asset asset = new Asset();

        asset.setName(name);
        asset.setDescription(description);
        asset.setStatus(status);
        asset.setSerialNumber(serialNumber);
        asset.setCategory(category);
        asset.setBase(base);

        assetRepository.save(asset);
    }
}