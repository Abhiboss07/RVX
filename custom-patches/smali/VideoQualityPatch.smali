.class public Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;
.super Ljava/lang/Object;
.source "VideoQualityPatch.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;
    }
.end annotation


# static fields
.field public static final AUTOMATIC_VIDEO_QUALITY_VALUE:I = -0x2

.field private static final HIDE_VIDEO_ADS:Z

.field private static currentFormats:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;",
            ">;"
        }
    .end annotation
.end field

.field private static currentMenuInterface:Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;

.field private static currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

.field private static currentQuality:Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

.field private static preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

.field private static qualityNeedsUpdating:Z

.field private static final shortsQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

.field private static final shortsQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

.field private static userChangedQuality:Z

.field private static final videoQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

.field private static final videoQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;


# direct methods
.method public static synthetic $r8$lambda$1-vWInotHUCtlLPUD3nHanJTr9g(Ljava/util/List;)Ljava/lang/String;
    .registers 3

    .line 227
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "VideoFormats: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$31QEFKAK-Xy3tj78RalG15j75SI(ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .registers 4

    if-eqz p0, :cond_19

    .line 253
    new-instance p0, Ljava/lang/StringBuilder;

    const-string v0, "Changing video format from: "

    invoke-direct {p0, v0}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p1, " to: "

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0

    .line 254
    :cond_19
    new-instance p0, Ljava/lang/StringBuilder;

    const-string p2, "Video format already has the preferred quality: "

    invoke-direct {p0, p2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$8jQURXSsZlwRIvlvzM9zykRppIA(I)Ljava/lang/String;
    .registers 3

    .line 188
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "initialVideoQuality: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$AwbWic5McYD0ABJ4jon_alwmEhY()Ljava/lang/String;
    .registers 1

    .line 332
    const-string v0, "setVideoQuality failure"

    return-object v0
.end method

.method public static synthetic $r8$lambda$Dsnc7LOhrSaCWDfKMgUhOaHjUlU(Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)Ljava/lang/String;
    .registers 3

    .line 109
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "Current quality changed to: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$E85UgOheDT0wlcnBLwxbZPaQtb4(IIII)Ljava/lang/String;
    .registers 6

    .line 472
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "Higher fps video quality already exists: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p0, " ("

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p1, "fps), removes lower fps video quality: "

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p0, "fps)"

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$EaeC6YiSScy-FS8ZMaAz5j5q2s0()Ljava/lang/String;
    .registers 1

    .line 348
    const-string v0, "Cannot save default quality, qualities is null"

    return-object v0
.end method

.method public static synthetic $r8$lambda$H2Kfk5rp4lYuUBT7WEUZMkun-uo()Ljava/lang/String;
    .registers 1

    .line 419
    const-string v0, "fixVideoQualityResolution failed"

    return-object v0
.end method

.method public static synthetic $r8$lambda$LLPYewdK6dN0DFCR2UnwoqlcD9g()Ljava/lang/String;
    .registers 1

    .line 480
    const-string v0, "removeLowFpsVideoQualities failure"

    return-object v0
.end method

.method public static synthetic $r8$lambda$M42tW2j7tkoIzaGc_Z9d7mrfhQ8()Ljava/lang/String;
    .registers 1

    .line 112
    const-string v0, "setCurrentQuality failed"

    return-object v0
.end method

.method public static synthetic $r8$lambda$NFIJbQpLGt-h06WFMFIS5WlclK8()Ljava/lang/String;
    .registers 1

    .line 229
    const-string v0, "setVideoFormat failure"

    return-object v0
.end method

.method public static synthetic $r8$lambda$lcfoE6JGPnPmMbUS4gJ79XWiihE(ZLcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)Ljava/lang/String;
    .registers 4

    if-eqz p0, :cond_19

    .line 311
    new-instance p0, Ljava/lang/StringBuilder;

    const-string v0, "Changing video quality from: "

    invoke-direct {p0, v0}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    const-string p1, " to: "

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0

    .line 312
    :cond_19
    new-instance p0, Ljava/lang/StringBuilder;

    const-string p1, "Video is already the preferred quality: "

    invoke-direct {p0, p1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$mPN7shd1vftzKmnVHywdv3AvND8()Ljava/lang/String;
    .registers 1

    .line 258
    const-string v0, "getVideoFormat failure"

    return-object v0
.end method

.method public static synthetic $r8$lambda$q-4U0g7UYxSZesmInQhplJ8soM8(ILjava/lang/String;I)Ljava/lang/String;
    .registers 5

    .line 414
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "Changing wrong quality resolution from: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p0, " ("

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, ") to: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p0, ")"

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$rkkgXMCKl0t4HLjlZzEjQhLMFBI(IIII)Ljava/lang/String;
    .registers 6

    .line 469
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "Higher fps video quality already exists: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p0, " ("

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p1, "fps), removes lower fps video quality: "

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p0, "fps)"

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static synthetic $r8$lambda$rwniR4RM7zhWZPpjAJUwv_t3_pY()Ljava/lang/String;
    .registers 1

    .line 377
    const-string v0, "newVideoStarted"

    return-object v0
.end method

.method public static synthetic $r8$lambda$sD9N9qbyIhp2PfzcKYTjtMZe2Z0()Ljava/lang/String;
    .registers 1

    .line 354
    const-string v0, "userChangedQualityInOldFlyout failure"

    return-object v0
.end method

.method public static synthetic $r8$lambda$uRW9hnCm0thqdR2-QNw_wrbLuD0()Ljava/lang/String;
    .registers 2

    .line 279
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "VideoQualities: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    invoke-static {v1}, Ljava/util/Arrays;->toString([Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method static constructor <clinit>()V
    .registers 1

    .line 47
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->HIDE_VIDEO_ADS:Lapp/morphe/extension/shared/settings/BooleanSetting;

    invoke-virtual {v0}, Lapp/morphe/extension/shared/settings/BooleanSetting;->get()Ljava/lang/Boolean;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    sput-boolean v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->HIDE_VIDEO_ADS:Z

    .line 49
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->DEFAULT_VIDEO_QUALITY_MOBILE_SHORTS:Lapp/morphe/extension/shared/settings/IntegerSetting;

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shortsQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

    .line 50
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->DEFAULT_VIDEO_QUALITY_WIFI_SHORTS:Lapp/morphe/extension/shared/settings/IntegerSetting;

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shortsQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

    .line 51
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->DEFAULT_VIDEO_QUALITY_MOBILE:Lapp/morphe/extension/shared/settings/IntegerSetting;

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->videoQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

    .line 52
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->DEFAULT_VIDEO_QUALITY_WIFI:Lapp/morphe/extension/shared/settings/IntegerSetting;

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->videoQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static fixVideoQualityResolution(Ljava/lang/String;I)I
    .registers 6

    if-lez p1, :cond_3c

    if-eqz p0, :cond_3c

    .line 409
    invoke-static {p1}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p0, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-nez v0, :cond_3c

    const/4 v0, 0x2

    .line 411
    :try_start_f
    new-array v0, v0, [Ljava/lang/CharSequence;

    const-string v1, "p"

    const/4 v2, 0x0

    aput-object v1, v0, v2

    const-string v1, "s"

    const/4 v3, 0x1

    aput-object v1, v0, v3

    invoke-static {p0, v0}, Lorg/apache/commons/lang3/StringUtils;->indexOfAny(Ljava/lang/CharSequence;[Ljava/lang/CharSequence;)I

    move-result v0

    const/4 v1, -0x1

    if-le v0, v1, :cond_3c

    .line 413
    invoke-static {p0, v2, v0}, Lorg/apache/commons/lang3/StringUtils;->substring(Ljava/lang/String;II)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v0

    .line 414
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda3;

    invoke-direct {v1, p1, p0, v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda3;-><init>(ILjava/lang/String;I)V

    invoke-static {v1}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V
    :try_end_32
    .catch Ljava/lang/Exception; {:try_start_f .. :try_end_32} :catch_33

    return v0

    :catch_33
    move-exception p0

    .line 419
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda4;

    invoke-direct {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda4;-><init>()V

    invoke-static {v0, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    :cond_3c
    return p1
.end method

.method public static getCurrentMenuInterface()Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;
    .registers 1

    .line 100
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentMenuInterface:Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;

    return-object v0
.end method

.method public static getCurrentQualities()[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;
    .registers 1

    .line 90
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    return-object v0
.end method

.method public static getCurrentQuality()Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;
    .registers 1

    .line 95
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQuality:Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    return-object v0
.end method

.method public static getDefaultQualityResolution()I
    .registers 3

    .line 128
    invoke-static {}, Lapp/morphe/extension/youtube/patches/utils/PatchStatus;->VideoPlayback()Z

    move-result v0

    if-nez v0, :cond_8

    const/4 v0, -0x2

    return v0

    .line 131
    :cond_8
    invoke-static {}, Lapp/morphe/extension/youtube/shared/RootView;->isShortsActive()Z

    move-result v0

    .line 132
    invoke-static {}, Lapp/morphe/extension/shared/utils/Utils;->getNetworkType()Lapp/morphe/extension/shared/utils/Utils$NetworkType;

    move-result-object v1

    sget-object v2, Lapp/morphe/extension/shared/utils/Utils$NetworkType;->MOBILE:Lapp/morphe/extension/shared/utils/Utils$NetworkType;

    if-ne v1, v2, :cond_1c

    if-eqz v0, :cond_19

    .line 133
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shortsQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

    goto :goto_23

    :cond_19
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->videoQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

    goto :goto_23

    :cond_1c
    if-eqz v0, :cond_21

    .line 134
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shortsQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

    goto :goto_23

    :cond_21
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->videoQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

    .line 135
    :goto_23
    invoke-virtual {v0}, Lapp/morphe/extension/shared/settings/IntegerSetting;->get()Ljava/lang/Integer;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    return v0
.end method

.method public static getInitialVideoQuality(Lj$/util/Optional;)Lj$/util/Optional;
    .registers 3

    .line 185
    invoke-static {}, Lapp/morphe/extension/youtube/patches/utils/PatchStatus;->VideoPlayback()Z

    move-result v0

    if-eqz v0, :cond_1d

    .line 186
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getDefaultQualityResolution()I

    move-result v0

    const/4 v1, -0x2

    if-eq v0, v1, :cond_1d

    .line 188
    new-instance p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda12;

    invoke-direct {p0, v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda12;-><init>(I)V

    invoke-static {p0}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    .line 190
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p0

    invoke-static {p0}, Lj$/util/Optional;->of(Ljava/lang/Object;)Lj$/util/Optional;

    move-result-object p0

    :cond_1d
    return-object p0
.end method

.method private static getQualityNameWithITag(Ljava/lang/String;I)Ljava/lang/String;
    .registers 3

    .line 171
    sget-object v0, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p1

    filled-new-array {p0, p1}, [Ljava/lang/Object;

    move-result-object p0

    const-string p1, "%s (itag: %d)"

    invoke-static {v0, p1, p0}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static getVideoFormat(Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;)Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;
    .registers 5

    .line 243
    invoke-static {}, Lapp/morphe/extension/youtube/patches/utils/PatchStatus;->VideoPlayback()Z

    move-result v0

    if-eqz v0, :cond_45

    if-eqz p0, :cond_45

    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->userChangedQuality:Z

    if-nez v0, :cond_45

    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    if-eqz v0, :cond_45

    .line 245
    :try_start_10
    invoke-virtual {p0}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getQualityName()Ljava/lang/String;

    move-result-object v0

    .line 246
    sget-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    invoke-virtual {v1}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getQualityName()Ljava/lang/String;

    move-result-object v1

    .line 247
    invoke-virtual {p0}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getITag()I

    move-result v2

    .line 248
    sget-object v3, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    invoke-virtual {v3}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getITag()I

    move-result v3

    .line 249
    invoke-static {v0, v2}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getQualityNameWithITag(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v0

    .line 250
    invoke-static {v1, v3}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getQualityNameWithITag(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v1

    if-eq v2, v3, :cond_30

    const/4 v2, 0x1

    goto :goto_31

    :cond_30
    const/4 v2, 0x0

    .line 252
    :goto_31
    new-instance v3, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda16;

    invoke-direct {v3, v2, v0, v1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda16;-><init>(ZLjava/lang/String;Ljava/lang/String;)V

    invoke-static {v3}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    .line 256
    sget-object p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;
    :try_end_3b
    .catch Ljava/lang/Exception; {:try_start_10 .. :try_end_3b} :catch_3c

    return-object p0

    :catch_3c
    move-exception v0

    .line 258
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda17;

    invoke-direct {v1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda17;-><init>()V

    invoke-static {v1, v0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    :cond_45
    return-object p0
.end method

.method public static newVideoStarted()V
    .registers 2

    .line 375
    invoke-static {}, Lapp/morphe/extension/shared/utils/Utils;->verifyOnMainThread()V

    .line 377
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda13;

    invoke-direct {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda13;-><init>()V

    invoke-static {v0}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    const/4 v0, 0x0

    .line 378
    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentFormats:Ljava/util/List;

    .line 379
    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    .line 380
    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQuality:Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    .line 381
    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentMenuInterface:Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;

    .line 382
    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    const/4 v1, 0x1

    .line 383
    sput-boolean v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->qualityNeedsUpdating:Z

    const/4 v1, 0x0

    .line 384
    sput-boolean v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->userChangedQuality:Z

    .line 387
    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->updateQualityString(Ljava/lang/String;)V

    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->cancelWatchdog()V

    return-void
.end method

.method public static removeLowFpsVideoQualities(Ljava/util/List;)Ljava/util/List;
    .registers 8
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;",
            ">;)",
            "Ljava/util/List<",
            "Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;",
            ">;"
        }
    .end annotation

    if-eqz p0, :cond_63

    .line 438
    invoke-interface {p0}, Ljava/util/List;->size()I

    move-result v0

    const/4 v1, 0x2

    if-le v0, v1, :cond_63

    .line 443
    :try_start_9
    invoke-interface {p0}, Ljava/util/List;->size()I

    move-result v0

    add-int/lit8 v0, v0, -0x1

    const/4 v1, -0x1

    move v2, v1

    :goto_11
    if-ltz v0, :cond_63

    .line 444
    invoke-interface {p0, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    .line 447
    invoke-virtual {v3}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getResolution()I

    move-result v4

    if-gez v4, :cond_20

    goto :goto_58

    .line 452
    :cond_20
    invoke-virtual {v3}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getQualityName()Ljava/lang/String;

    move-result-object v5

    if-nez v5, :cond_27

    goto :goto_58

    .line 454
    :cond_27
    const-string v6, "Premium"

    invoke-virtual {v5, v6}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v5

    if-eqz v5, :cond_30

    goto :goto_58

    .line 460
    :cond_30
    invoke-virtual {v3}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getFps()I

    move-result v3

    if-ne v1, v4, :cond_56

    if-le v2, v3, :cond_46

    .line 468
    invoke-interface {p0, v0}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    .line 469
    new-instance v5, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda9;

    invoke-direct {v5, v1, v2, v4, v3}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda9;-><init>(IIII)V

    invoke-static {v5}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    goto :goto_58

    :catch_44
    move-exception v0

    goto :goto_5b

    :cond_46
    if-ge v2, v3, :cond_58

    add-int/lit8 v5, v0, 0x1

    .line 471
    invoke-interface {p0, v5}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    .line 472
    new-instance v5, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda10;

    invoke-direct {v5, v4, v3, v1, v2}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda10;-><init>(IIII)V

    invoke-static {v5}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V
    :try_end_55
    .catch Ljava/lang/Exception; {:try_start_9 .. :try_end_55} :catch_44

    goto :goto_58

    :cond_56
    move v2, v3

    move v1, v4

    :cond_58
    :goto_58
    add-int/lit8 v0, v0, -0x1

    goto :goto_11

    .line 480
    :goto_5b
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda11;

    invoke-direct {v1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda11;-><init>()V

    invoke-static {v1, v0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    :cond_63
    return-object p0
.end method

.method public static saveDefaultQuality(I)V
    .registers 5

    .line 139
    invoke-static {}, Lapp/morphe/extension/youtube/shared/RootView;->isShortsActive()Z

    move-result v0

    .line 142
    invoke-static {}, Lapp/morphe/extension/shared/utils/Utils;->getNetworkType()Lapp/morphe/extension/shared/utils/Utils$NetworkType;

    move-result-object v1

    sget-object v2, Lapp/morphe/extension/shared/utils/Utils$NetworkType;->MOBILE:Lapp/morphe/extension/shared/utils/Utils$NetworkType;

    if-ne v1, v2, :cond_1a

    .line 143
    const-string v1, "revanced_remember_video_quality_mobile"

    invoke-static {v1}, Lapp/morphe/extension/shared/utils/StringRef;->str(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    if-eqz v0, :cond_17

    .line 144
    sget-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shortsQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

    goto :goto_27

    :cond_17
    sget-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->videoQualityMobile:Lapp/morphe/extension/shared/settings/IntegerSetting;

    goto :goto_27

    .line 146
    :cond_1a
    const-string v1, "revanced_remember_video_quality_wifi"

    invoke-static {v1}, Lapp/morphe/extension/shared/utils/StringRef;->str(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    if-eqz v0, :cond_25

    .line 147
    sget-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shortsQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

    goto :goto_27

    :cond_25
    sget-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->videoQualityWifi:Lapp/morphe/extension/shared/settings/IntegerSetting;

    .line 150
    :goto_27
    invoke-virtual {v2}, Lapp/morphe/extension/shared/settings/IntegerSetting;->get()Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    if-ne v3, p0, :cond_32

    goto :goto_68

    .line 155
    :cond_32
    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v2, v3}, Lapp/morphe/extension/shared/settings/Setting;->save(Ljava/lang/Object;)V

    .line 157
    sget-object v2, Lapp/morphe/extension/youtube/settings/Settings;->REMEMBER_VIDEO_QUALITY_LAST_SELECTED_TOAST:Lapp/morphe/extension/shared/settings/BooleanSetting;

    invoke-virtual {v2}, Lapp/morphe/extension/shared/settings/BooleanSetting;->get()Ljava/lang/Boolean;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v2

    if-eqz v2, :cond_68

    .line 158
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p0, "p"

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    if-eqz v0, :cond_5b

    .line 161
    const-string v0, "revanced_remember_video_quality_toast_shorts"

    goto :goto_5d

    .line 162
    :cond_5b
    const-string v0, "revanced_remember_video_quality_toast"

    :goto_5d
    filled-new-array {v1, p0}, [Ljava/lang/Object;

    move-result-object p0

    .line 159
    invoke-static {v0, p0}, Lapp/morphe/extension/shared/utils/StringRef;->str(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p0

    invoke-static {p0}, Lapp/morphe/extension/shared/utils/Utils;->showToastShort(Ljava/lang/String;)V

    :cond_68
    :goto_68
    return-void
.end method

.method public static setCurrentQuality(Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)V
    .registers 3

    .line 105
    :try_start_0
    invoke-virtual {p0}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getResolution()I

    move-result v0

    const/4 v1, -0x2

    if-eq v0, v1, :cond_1e

    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQuality:Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    if-eqz v0, :cond_d

    if-eq v0, p0, :cond_1e

    .line 107
    :cond_d
    sput-object p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQuality:Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    .line 108
    invoke-virtual {p0}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getQualityName()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->updateQualityString(Ljava/lang/String;)V

    .line 109
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda5;

    invoke-direct {v0, p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda5;-><init>(Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)V

    invoke-static {v0}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    invoke-virtual {p0}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getResolution()I

    move-result v0

    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->startWatchdog(I)V
    :try_end_1e
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_1e} :catch_1f

    :cond_1e
    return-void

    :catch_1f
    move-exception p0

    .line 112
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda6;

    invoke-direct {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda6;-><init>()V

    invoke-static {v0, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    return-void
.end method

.method public static setVideoFormat(Ljava/util/List;)V
    .registers 7
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;",
            ">;)V"
        }
    .end annotation

    .line 202
    invoke-static {}, Lapp/morphe/extension/youtube/patches/utils/PatchStatus;->VideoPlayback()Z

    move-result v0

    if-eqz v0, :cond_7c

    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->userChangedQuality:Z

    if-nez v0, :cond_7c

    invoke-static {p0}, Lorg/apache/commons/collections4/CollectionUtils;->isEmpty(Ljava/util/Collection;)Z

    move-result v0

    if-nez v0, :cond_7c

    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentFormats:Ljava/util/List;

    if-eqz v0, :cond_1a

    .line 203
    invoke-static {v0, p0}, Lorg/apache/commons/collections4/CollectionUtils;->isEqualCollection(Ljava/util/Collection;Ljava/util/Collection;)Z

    move-result v0

    if-nez v0, :cond_7c

    .line 207
    :cond_1a
    :try_start_1a
    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->HIDE_VIDEO_ADS:Z

    const/4 v1, 0x1

    if-eqz v0, :cond_70

    invoke-static {}, Lapp/morphe/extension/youtube/shared/RootView;->isShortsActive()Z

    move-result v0

    if-eqz v0, :cond_26

    goto :goto_70

    .line 211
    :cond_26
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getDefaultQualityResolution()I

    move-result v0

    const/4 v2, -0x2

    if-ne v0, v2, :cond_30

    .line 213
    sput-boolean v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->userChangedQuality:Z

    return-void

    .line 216
    :cond_30
    sput-object p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentFormats:Ljava/util/List;

    .line 217
    new-instance v1, Ljava/util/ArrayList;

    invoke-interface {p0}, Ljava/util/List;->size()I

    move-result v2

    invoke-direct {v1, v2}, Ljava/util/ArrayList;-><init>(I)V

    .line 218
    invoke-interface {p0}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_3f
    :goto_3f
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_67

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    .line 219
    invoke-virtual {v2}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getITag()I

    move-result v3

    .line 220
    invoke-virtual {v2}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getQualityName()Ljava/lang/String;

    move-result-object v4

    .line 221
    invoke-virtual {v2}, Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;->patch_getResolution()I

    move-result v5

    .line 222
    invoke-static {v4, v3}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getQualityNameWithITag(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v3}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 223
    sget-object v3, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    if-nez v3, :cond_3f

    if-gt v5, v0, :cond_3f

    .line 224
    sput-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->preferredFormat:Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;

    goto :goto_3f

    .line 227
    :cond_67
    new-instance p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda7;

    invoke-direct {p0, v1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda7;-><init>(Ljava/util/List;)V

    invoke-static {p0}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    return-void

    .line 208
    :cond_70
    :goto_70
    sput-boolean v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->userChangedQuality:Z
    :try_end_72
    .catch Ljava/lang/Exception; {:try_start_1a .. :try_end_72} :catch_73

    return-void

    :catch_73
    move-exception p0

    .line 229
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda8;

    invoke-direct {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda8;-><init>()V

    invoke-static {v0, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    :cond_7c
    return-void
.end method

.method public static setVideoQuality([Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;I)I
    .registers 13

    .line 272
    :try_start_0
    invoke-static {}, Lapp/morphe/extension/shared/utils/Utils;->verifyOnMainThread()V

    .line 273
    sput-object p1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentMenuInterface:Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;

    .line 275
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    const/4 v1, 0x1

    const/4 v2, 0x0

    if-eqz v0, :cond_16

    .line 276
    invoke-static {v0, p0}, Ljava/util/Arrays;->equals([Ljava/lang/Object;[Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_12

    goto :goto_16

    :cond_12
    move v0, v2

    goto :goto_17

    :catch_14
    move-exception p0

    goto :goto_77

    :cond_16
    :goto_16
    move v0, v1

    :goto_17
    if-eqz v0, :cond_23

    .line 278
    sput-object p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    .line 279
    new-instance v3, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda0;

    invoke-direct {v3}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda0;-><init>()V

    invoke-static {v3}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    .line 285
    :cond_23
    invoke-static {p2, v2}, Ljava/lang/Math;->max(II)I

    move-result p2

    .line 287
    aget-object v3, p0, p2

    .line 288
    invoke-static {v3}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->setCurrentQuality(Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)V

    invoke-virtual {v3}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getResolution()I

    move-result v4

    invoke-static {v4}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->notifyQualityChanged(I)V

    .line 290
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getDefaultQualityResolution()I

    move-result v4

    const/4 v5, -0x2

    if-ne v4, v5, :cond_34

    goto :goto_76

    .line 297
    :cond_34
    sget-boolean v6, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->qualityNeedsUpdating:Z

    if-nez v6, :cond_3b

    if-nez v0, :cond_3b

    goto :goto_76

    .line 300
    :cond_3b
    sput-boolean v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->qualityNeedsUpdating:Z

    .line 304
    array-length v0, p0

    move v6, v2

    move v7, v6

    :goto_40
    if-ge v6, v0, :cond_76

    aget-object v8, p0, v6

    .line 305
    invoke-virtual {v8}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getResolution()I

    move-result v9

    if-eq v9, v5, :cond_4c

    if-le v9, v4, :cond_50

    .line 306
    :cond_4c
    array-length v9, p0

    sub-int/2addr v9, v1

    if-ne v7, v9, :cond_71

    :cond_50
    if-eq v7, p2, :cond_53

    goto :goto_54

    :cond_53
    move v1, v2

    .line 310
    :goto_54
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda1;

    invoke-direct {v0, v1, v3, v8}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda1;-><init>(ZLcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)V

    invoke-static {v0}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    if-nez v1, :cond_64

    .line 320
    invoke-static {}, Lapp/morphe/extension/youtube/shared/RootView;->isShortsActive()Z

    move-result v0

    if-nez v0, :cond_76

    .line 321
    :cond_64
    invoke-virtual {v8}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getQualityName()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->updateQualityString(Ljava/lang/String;)V

    .line 322
    aget-object p0, p0, v7

    invoke-interface {p1, p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$VideoQualityMenuInterface;->patch_setQuality(Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;)V
    :try_end_70
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_70} :catch_14

    return v7

    :cond_71
    add-int/lit8 v7, v7, 0x1

    add-int/lit8 v6, v6, 0x1

    goto :goto_40

    :cond_76
    :goto_76
    return p2

    .line 332
    :goto_77
    new-instance p1, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda2;

    invoke-direct {p1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda2;-><init>()V

    invoke-static {p1, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    return p2
.end method

.method public static shouldRememberVideoQuality()Z
    .registers 1

    const/4 v0, 0x1

    .line 117
    sput-boolean v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->userChangedQuality:Z

    .line 118
    invoke-static {}, Lapp/morphe/extension/youtube/patches/utils/PatchStatus;->VideoPlayback()Z

    move-result v0

    if-nez v0, :cond_b

    const/4 v0, 0x0

    return v0

    .line 121
    :cond_b
    invoke-static {}, Lapp/morphe/extension/youtube/shared/RootView;->isShortsActive()Z

    move-result v0

    if-eqz v0, :cond_14

    .line 122
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->REMEMBER_VIDEO_QUALITY_SHORTS_LAST_SELECTED:Lapp/morphe/extension/shared/settings/BooleanSetting;

    goto :goto_16

    .line 123
    :cond_14
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->REMEMBER_VIDEO_QUALITY_LAST_SELECTED:Lapp/morphe/extension/shared/settings/BooleanSetting;

    .line 124
    :goto_16
    invoke-virtual {v0}, Lapp/morphe/extension/shared/settings/BooleanSetting;->get()Ljava/lang/Boolean;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    return v0
.end method

.method public static updateQualityString(Ljava/lang/String;)V
    .registers 1

    .line 394
    invoke-static {p0}, Lapp/morphe/extension/youtube/utils/VideoUtils;->updateQualityString(Ljava/lang/String;)V

    return-void
.end method

.method public static userChangedQualityInNewFlyout(I)V
    .registers 2

    .line 365
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shouldRememberVideoQuality()Z

    move-result v0

    if-eqz v0, :cond_c

    .line 366
    invoke-static {}, Lapp/morphe/extension/shared/utils/Utils;->verifyOnMainThread()V

    .line 367
    invoke-static {p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->saveDefaultQuality(I)V

    :cond_c
    return-void
.end method

.method public static userChangedQualityInOldFlyout(I)V
    .registers 2

    .line 345
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->shouldRememberVideoQuality()Z

    move-result v0

    if-eqz v0, :cond_26

    .line 347
    :try_start_6
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->currentQualities:[Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    if-nez v0, :cond_13

    .line 348
    new-instance p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda14;

    invoke-direct {p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda14;-><init>()V

    invoke-static {p0}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    return-void

    .line 351
    :cond_13
    aget-object p0, v0, p0

    .line 352
    invoke-virtual {p0}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getResolution()I

    move-result p0

    invoke-static {p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->saveDefaultQuality(I)V
    :try_end_1c
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_1c} :catch_1d

    return-void

    :catch_1d
    move-exception p0

    .line 354
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda15;

    invoke-direct {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch$$ExternalSyntheticLambda15;-><init>()V

    invoke-static {v0, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    :cond_26
    return-void
.end method
