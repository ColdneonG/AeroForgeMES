/** Formats a numeric value for display without changing the underlying data. */
export function formatDisplayNumber(value: unknown, locale = 'zh-CN') {
  const number = typeof value === 'number' ? value : typeof value === 'string' && /^-?\d+(?:\.\d+)?$/.test(value.trim()) ? Number(value) : NaN
  if (!Number.isFinite(number)) return String(value ?? '-')

  return Number.isInteger(number)
    ? new Intl.NumberFormat(locale, { maximumFractionDigits: 0 }).format(number)
    : new Intl.NumberFormat(locale, { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(number)
}

export function formatDisplayValue(value: unknown) {
  return value === undefined || value === null || value === '' ? '-' : formatDisplayNumber(value)
}

export function formatPercent(value: unknown) {
  const number = Number(value)
  return `${Number.isFinite(number) ? number.toFixed(2) : '0.00'}%`
}
