import type { PiniaPluginContext } from 'pinia'

export function persistToLocalStorage(targetStores: string[] = []) {
    return ({ store }: PiniaPluginContext) => {

        if (!targetStores.includes(store.$id)) return;

        const storageKey = `pinia-${store.$id}`;
        const saved = localStorage.getItem(storageKey);

        if (saved) {
            try {
                store.$patch(JSON.parse(saved))
            } catch (e) {
                console.warn(`Failed to load persisted data for ${store.$id}`, e);
            }
        }

        store.$subscribe((_mutation, state) => {
            localStorage.setItem(storageKey, JSON.stringify(state));
        });
    }
}
