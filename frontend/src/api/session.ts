const ACCESS_TOKEN_KEY = 'fanmes.accessToken'
const USER_KEY = 'fanmes.user'
let accessToken: string | null = null

// Credentials are deliberately kept only in memory so every new app load
// starts at the login page. Remove tokens created by older versions as well.
localStorage.removeItem(ACCESS_TOKEN_KEY)
localStorage.removeItem(USER_KEY)

type JwtPayload = { exp?: unknown }

function decodeJwtPayload(token: string): JwtPayload | null {
  const payload = token.split('.')[1]
  if (!payload) return null

  try {
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(
      Array.from(atob(base64), (character) => `%${character.charCodeAt(0).toString(16).padStart(2, '0')}`).join(''),
    )
    return JSON.parse(json) as JwtPayload
  } catch {
    return null
  }
}

/** Returns true when the JWT is malformed, missing its expiry, or has expired. */
export function isAccessTokenExpired(token: string | null = accessToken) {
  if (!token) return true
  const exp = decodeJwtPayload(token)?.exp
  return typeof exp !== 'number' || !Number.isFinite(exp) || Date.now() >= exp * 1000
}

export function getValidAccessToken() {
  return isAccessTokenExpired(accessToken) ? null : accessToken
}

export function hasValidSession() {
  return Boolean(getValidAccessToken())
}

export function startSession(token: string) {
  accessToken = token
}

export function clearSession() {
  accessToken = null
}
