# Reglas de R8 para el APK de release.
# Gson lee los DTO y los modelos por reflexión, por eso no se les cambia el nombre ni se recortan.
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod
-keep class cl.duoc.aurasacrapp.remote.dto.** { *; }
-keep class cl.duoc.aurasacrapp.model.** { *; }
