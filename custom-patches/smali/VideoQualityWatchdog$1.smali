.class Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1;
.super Ljava/lang/Object;
.source "VideoQualityWatchdog.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->startWatchdog(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic val$targetResolution:I


# direct methods
.method constructor <init>(I)V
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .line 18
    iput p1, p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1;->val$targetResolution:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method static synthetic lambda$run$0(I)Ljava/lang/String;
    .registers 3

    .line 22
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "VideoQualityWatchdog: Quality switch to "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string v0, "p timed out (5s)"

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method static synthetic lambda$run$1()Ljava/lang/String;
    .registers 1

    .line 29
    const-string v0, "VideoQualityWatchdog: Triggering player reload to recover stream"

    return-object v0
.end method

.method static synthetic lambda$run$2()Ljava/lang/String;
    .registers 1

    .line 32
    const-string v0, "VideoQualityWatchdog execution failed"

    return-object v0
.end method


# virtual methods
.method public run()V
    .registers 5

    .line 22
    const/4 v0, 0x0

    const/4 v1, -0x1

    :try_start_2
    iget v2, p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1;->val$targetResolution:I

    new-instance v3, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1$$ExternalSyntheticLambda0;

    invoke-direct {v3, v2}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1$$ExternalSyntheticLambda0;-><init>(I)V

    invoke-static {v3}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    .line 24
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->getCurrentQuality()Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;

    move-result-object v2

    .line 25
    if-eqz v2, :cond_19

    .line 26
    invoke-virtual {v2}, Lcom/google/android/libraries/youtube/innertube/model/media/VideoQuality;->patch_getQualityName()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Lapp/morphe/extension/youtube/patches/video/VideoQualityPatch;->updateQualityString(Ljava/lang/String;)V

    .line 29
    :cond_19
    new-instance v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1$$ExternalSyntheticLambda1;

    invoke-direct {v2}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1$$ExternalSyntheticLambda1;-><init>()V

    invoke-static {v2}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    .line 30
    invoke-static {}, Lapp/morphe/extension/youtube/utils/VideoUtils;->reloadVideo()V
    :try_end_24
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_24} :catch_27
    .catchall {:try_start_2 .. :try_end_24} :catchall_25

    goto :goto_30

    .line 34
    :catchall_25
    move-exception v2

    goto :goto_38

    .line 31
    :catch_27
    move-exception v2

    .line 32
    :try_start_28
    new-instance v3, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1$$ExternalSyntheticLambda2;

    invoke-direct {v3}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1$$ExternalSyntheticLambda2;-><init>()V

    invoke-static {v3, v2}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V
    :try_end_30
    .catchall {:try_start_28 .. :try_end_30} :catchall_25

    .line 34
    :goto_30
    invoke-static {v1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->-$$Nest$sfputpendingResolution(I)V

    .line 35
    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->-$$Nest$sfputwatchdogRunnable(Ljava/lang/Runnable;)V

    .line 36
    nop

    .line 37
    return-void

    .line 34
    :goto_38
    invoke-static {v1}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->-$$Nest$sfputpendingResolution(I)V

    .line 35
    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->-$$Nest$sfputwatchdogRunnable(Ljava/lang/Runnable;)V

    .line 36
    throw v2
.end method
