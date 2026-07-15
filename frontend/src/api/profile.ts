import { get, put } from './client'

export interface UserProfile {
  userId: number
  username: string
  displayName: string
  mobile: string | null
  employeeNo: string | null
  orgId: number | null
  orgName: string | null
  teamId: number | null
  teamName: string | null
  status: string | null
  roles: string[]
  permissions: string[]
}

export interface ProfileUpdateInput {
  displayName: string
  mobile?: string
}

export interface PasswordUpdateInput {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export function getProfile() {
  return get<UserProfile>('/auth/profile')
}

export function updateProfile(data: ProfileUpdateInput) {
  return put<UserProfile>('/auth/profile', data)
}

export function updatePassword(data: PasswordUpdateInput) {
  return put<void>('/auth/profile/password', data)
}
