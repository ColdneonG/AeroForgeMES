import { reactive, ref } from 'vue'

export function usePageInteractions() {
  const traceVisible = ref(false)
  const scanValue = ref('')
  const scanResult = reactive({
    visible: false,
    barcode: '',
    type: '产品条码',
    item: 'FS-40-B3 落地扇',
    order: 'MO-20260711-0032',
    batch: 'BAT-0711A',
    status: '已激活',
  })

  function notify(message: string) {
    window.alert(message)
  }

  function doScan() {
    const value = scanValue.value.trim()
    if (!value) return
    scanResult.barcode = value
    scanResult.visible = true
  }

  return { notify, traceVisible, scanValue, scanResult, doScan }
}
