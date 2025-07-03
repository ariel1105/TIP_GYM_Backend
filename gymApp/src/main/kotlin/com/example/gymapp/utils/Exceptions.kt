package com.example.gymapp.utils

class IncorrectVoucherException(): RuntimeException("Este voucher no es para esta actividad")
class NonOwnVoucherException(): RuntimeException("No puede usar este voucher")
class NoRemainingClassesException(activity: String): RuntimeException("No hay voucher válido para la actividad $activity")
class TurnAlreadyFullException(): RuntimeException("No puedes reservar porque el turno ya está lleno")
class NoTurnsForActivityException(): RuntimeException("No hay turnos para esta actividad, prueba mas tarde")
class UsernameAlreadyTakenException(val username: String) : RuntimeException("El usuario '$username' ya está registrado")
class MemberAlreadyRegisteredException() : RuntimeException("Ya estas registrado en este turno")
class MemberNotRegisteredInTurnException() : RuntimeException("No estas registrado en este turno")
class PassedTimeOnTurnException() : RuntimeException("Ya pasó el tiempo límite para la cancelación del turno")
class NonActiveBodyBuildingSubscriptionException(): RuntimeException("El miembro no tiene una suscripción activa")
class HaveAlreadyEntryBodyBuildingException(): RuntimeException("Ya registraste un ingreso hoy")
class NoDaysLeftInBodyBuildingSubscriptionException: RuntimeException("El miembro ya ha ingresado todos los días correspondientes a esta semana")