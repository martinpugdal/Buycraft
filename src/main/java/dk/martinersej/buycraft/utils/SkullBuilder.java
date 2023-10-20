package dk.martinersej.buycraft.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("CallToPrintStackTrace")
public class SkullBuilder extends ItemBuilder {
    private static Field metaProfileField;

    static {
        try {
            SkullMeta meta = (SkullMeta) new ItemStack(Material.SKULL_ITEM).getItemMeta();
            metaProfileField = meta.getClass().getDeclaredField("profile");
            metaProfileField.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    protected SkullBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    static boolean isSkull(ItemStack item) {
        return item.getType() == Material.SKULL_ITEM && item.getDurability() == 3;
    }

    private static void mutateItemMeta(SkullMeta meta, String b64) {
        try {
            metaProfileField.set(meta, makeProfile(b64));
        } catch (IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
    }

    private static GameProfile makeProfile(String b64) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "Player");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    public static String getSkullTexture(ItemStack item) {
        if (!isSkull(item)) {
            throw new IllegalArgumentException("getSkullTexture() only applicable for Skulls!");
        }
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        try {
            GameProfile profile = (GameProfile) metaProfileField.get(meta);
            if (profile != null) {
                Collection<Property> textures = profile.getProperties().get("textures");
                if (textures != null && !textures.isEmpty()) {
                    return textures.iterator().next().getValue();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemBuilder setSkullOwner(String owner) {
        if (isSkull(this.build())) {
            SkullMeta meta = (SkullMeta) build().getItemMeta();
            meta.setOwner(owner);
            this.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setSkullOwnerByBase64(String base64) {
        if (!(this.build().getItemMeta() instanceof SkullMeta)) {
            throw new IllegalArgumentException("setSkullOwnerByBase64() only applicable for Skulls!");
        }
        SkullMeta meta = (SkullMeta) this.build().getItemMeta();
        mutateItemMeta(meta, base64);
        this.setItemMeta(meta);
        return this;
    }
}