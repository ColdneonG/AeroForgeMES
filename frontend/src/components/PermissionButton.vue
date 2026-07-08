<template>
  <button
    class="mes-action-button"
    :class="{ 'is-disabled': disabledByRule }"
    :disabled="disabledByRule"
    :title="disabledReason"
    type="button"
    @click="$emit('click', $event)"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { hasButtonPermission } from '../stores/auth'
import { canTransition } from '../utils/statusFlow'

const { t } = useI18n()

const props = defineProps({
  permission: { type: String, default: '' },
  flowType: { type: String, default: '' },
  status: { type: String, default: '' },
  action: { type: String, default: '' },
  disabled: { type: Boolean, default: false }
})

defineEmits(['click'])

const allowedByPermission = computed(() => hasButtonPermission(props.permission))
const allowedByStatus = computed(() => {
  if (!props.flowType || !props.status || !props.action) return true
  return canTransition(props.flowType, props.status, props.action)
})

const disabledByRule = computed(
  () => props.disabled || !allowedByPermission.value || !allowedByStatus.value
)

const disabledReason = computed(() => {
  if (props.disabled) return t('common.button.disabledDefault')
  if (!allowedByPermission.value) return t('common.button.noPermission')
  if (!allowedByStatus.value) return t('common.button.statusNotAllowed')
  return ''
})
</script>
