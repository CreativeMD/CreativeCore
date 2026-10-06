package team.creative.creativecore;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommonRegistry {
	public static final CommonRegistry INSTANCE = new CommonRegistry();
	public final class NamespacedRegistry {
		private final String namespace;
		public NamespacedRegistry(String namespace) {
			this.namespace = namespace;
		}
		public String namespace() {
			return namespace;
		}
		public <T, I extends T, R extends Registry<T>> DeferredHolder<T, I> register(Supplier<Registry<T>> registrySupplier, ResourceKey<R> registryKey, String identifier, Function<ResourceKey<T>, I> sup) {
			return CommonRegistry.this.register(registrySupplier, registryKey, key -> ResourceKey.create(key, Identifier.fromNamespaceAndPath(namespace, identifier)), sup);
		}
		public <T, I extends T, R extends Registry<T>> DeferredHolder<T, I> register(Supplier<Registry<T>> registrySupplier, ResourceKey<R> registryKey, Identifier identifier, Function<ResourceKey<T>, I> sup) {
			return CommonRegistry.this.register(registrySupplier, registryKey, key -> ResourceKey.create(key, identifier), sup);
		}

		public BoundRegistry.Items createItems() {
			return new BoundRegistry.Items(this);
		}
		public <T, R extends Registry<T>> BoundRegistry<T> bindToRegistry(Supplier<Registry<T>> registrySupplier, ResourceKey<R> registryKey) {
			return new BoundRegistry<>(this, registrySupplier, registryKey);
		}
		public <T, R extends Registry<T>> BoundRegistry<T> bindToRegistry(Registry<T> registry) {
			return new BoundRegistry<>(this, new UnitSupplier<>(registry), registry.key());
		}
		public <T> TagKey<T> createTagKey(ResourceKey<? extends Registry<T>> registryKey, String id) {
			return TagKey.create(registryKey, Identifier.fromNamespaceAndPath(namespace, id));

		}
	}
	public sealed static class BoundRegistry<T> permits BoundRegistry.Items {
		public static final class Items extends BoundRegistry<Item> {
			private Items(NamespacedRegistry parent) {
				super(parent, () -> BuiltInRegistries.ITEM, Registries.ITEM);
			}

			public <I extends Item, R extends I> DeferredHolder<Item, I> registerItem(String identifier, Function<Item.Properties, R> func) {
				return this.register(identifier, key -> func.apply(new Item.Properties().setId(key)));
			}
		}
		private final NamespacedRegistry parent;
		private final Supplier<Registry<T>> registrySupplier;
		private final ResourceKey<? extends Registry<T>> registryKey;
		private BoundRegistry(NamespacedRegistry parent, Supplier<Registry<T>> registrySupplier, ResourceKey<? extends Registry<T>> registryKey) {
			this.parent = parent;
			this.registrySupplier = registrySupplier;
			this.registryKey = registryKey;
		}
		public <I extends T> DeferredHolder<T, I> register(String identifier, Function<ResourceKey<T>, I> sup) {
			return parent.register(registrySupplier, registryKey, identifier, sup);
		}
		public <I extends T> DeferredHolder<T, I> register(String identifier, Supplier<I> sup) {
			return parent.register(registrySupplier, registryKey, identifier, _ -> sup.get());
		}
		public TagKey<T> createTagKey(String identifier) {
			return parent.createTagKey(registryKey, identifier);
		}
		public <I extends T> DeferredHolder<T, I> register(Identifier identifier, Supplier<I> sup) {
			return parent.register(registrySupplier, registryKey, identifier, _ -> sup.get());
		}
	}
	public NamespacedRegistry bindToNamespace(String namespace) {
		return new NamespacedRegistry(namespace);
	}
	public sealed interface Holder<T> permits DeferredHolder {
		T value();
	}
	public sealed interface DeferredHolder<T, I extends T> extends Holder<T>, Supplier<I> permits Entry {
		I value();
		I get();
	}

	private record UnitSupplier<T>(T instance) implements Supplier<T> {
		@Override
		public T get() {
			return instance;
		}
	}

	private static final class Entry<T, I extends T, R extends Registry<T>> implements DeferredHolder<T, I> {
		private final Supplier<Registry<T>> registrySupplier;
		private final ResourceKey<R> registryKey;
		private final Function<ResourceKey<T>, I> factory;
		private final Function<ResourceKey<R>, ResourceKey<T>> key;

		private @Nullable I value;
		private boolean bound;
		private net.minecraft.core.@Nullable Holder<T> holder;
		private Entry(Supplier<Registry<T>> registrySupplier, ResourceKey<R> registryKey, Function<ResourceKey<R>, ResourceKey<T>> key, Function<ResourceKey<T>, I> factory) {
			this.registrySupplier = registrySupplier;
			this.registryKey = registryKey;
			this.key = key;
			this.factory = factory;
		}
		private void bind() {
			if(!bound) {
				this.holder = registrySupplier.get().getOrThrow(this.key.apply(registryKey));
			}
			bound = true;
		}

		private I computeValue() {
			return value = factory.apply(key.apply(registryKey));
		}
		@Override
		public I value() {
			return Objects.requireNonNull(value);
		}
		@Override
		public I get() {
			return Objects.requireNonNull(value);
		}

		private net.minecraft.core.Holder<T> getHolder() {
			this.bind();
			return Objects.requireNonNull(this.holder, () -> ("Trying to access unbound value: " + this.key.apply(registryKey)));
		}
	}
	private final List<Entry<?, ?, ?>> entries;
	private CommonRegistry() {
		this.entries = new ArrayList<>();
	}

	public <T, I extends T, R extends Registry<T>> DeferredHolder<T, I> register(Supplier<Registry<T>> registry, ResourceKey<R> registryKey, Function<ResourceKey<R>, ResourceKey<T>> resourceKey, Function<ResourceKey<T>, I> sup) {
		Entry<T, I, R> entry = new Entry<>(registry, registryKey, resourceKey, sup);
		entries.add(entry);
		return entry;
	}

	private static <T, I extends T, R extends Registry<T>> void register(Entry<T, I, R> entry, RegisterHelper helper) {
		helper.register(entry.registrySupplier.get(),entry.key.apply(entry.registryKey), entry::computeValue);
	}

	public void addEntries(RegisterHelper helper) {
		for(Entry<?, ?, ?> entry : entries) {
			register(entry, helper);
		}
	}
	public interface RegisterHelper {
		<T, I extends T> void register(Registry<T> registry, ResourceKey<T> name, Supplier<I> value);
	}
}