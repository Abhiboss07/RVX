.class public Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;
.super Ljava/lang/Object;
.source "VideoQualityWatchdog.java"


# static fields
.field private static final mainHandler:Landroid/os/Handler;

.field private static pendingResolution:I

.field private static watchdogRunnable:Ljava/lang/Runnable;


# direct methods
.method static bridge synthetic -$$Nest$sfputpendingResolution(I)V
    .registers 1

    sput p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->pendingResolution:I

    return-void
.end method

.method static bridge synthetic -$$Nest$sfputwatchdogRunnable(Ljava/lang/Runnable;)V
    .registers 1

    sput-object p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    return-void
.end method

.method static constructor <clinit>()V
    .registers 2

    .line 10
    new-instance v0, Landroid/os/Handler;

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->mainHandler:Landroid/os/Handler;

    .line 11
    const/4 v0, 0x0

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    .line 12
    const/4 v0, -0x1

    sput v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->pendingResolution:I

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static declared-synchronized cancelWatchdog()V
    .registers 3

    const-class v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;

    monitor-enter v0

    .line 52
    :try_start_3
    sget-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    if-eqz v1, :cond_11

    .line 53
    sget-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->mainHandler:Landroid/os/Handler;

    sget-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    invoke-virtual {v1, v2}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    .line 54
    const/4 v1, 0x0

    sput-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    .line 56
    :cond_11
    const/4 v1, -0x1

    sput v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->pendingResolution:I
    :try_end_14
    .catchall {:try_start_3 .. :try_end_14} :catchall_16

    .line 57
    monitor-exit v0

    return-void

    .line 51
    :catchall_16
    move-exception v1

    monitor-exit v0

    throw v1
.end method

.method static synthetic lambda$notifyQualityChanged$1(I)Ljava/lang/String;
    .registers 3

    .line 46
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "VideoQualityWatchdog: Successfully reached "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string v0, "p, canceling watchdog"

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method static synthetic lambda$startWatchdog$0(I)Ljava/lang/String;
    .registers 3

    .line 41
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "VideoQualityWatchdog: Started for "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object p0

    const-string v0, "p (timeout 5s)"

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public static declared-synchronized notifyQualityChanged(I)V
    .registers 3

    const-class v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;

    monitor-enter v0

    .line 45
    :try_start_3
    sget-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    if-eqz v1, :cond_12

    .line 46
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda0;

    invoke-direct {v1, p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda0;-><init>(I)V

    invoke-static {v1}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V

    .line 47
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->cancelWatchdog()V
    :try_end_12
    .catchall {:try_start_3 .. :try_end_12} :catchall_14

    .line 49
    :cond_12
    monitor-exit v0

    return-void

    .line 44
    :catchall_14
    move-exception p0

    monitor-exit v0

    throw p0
.end method

.method public static declared-synchronized startWatchdog(I)V
    .registers 6

    const-class v0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;

    monitor-enter v0

    .line 15
    :try_start_3
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->cancelWatchdog()V

    .line 16
    sput p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->pendingResolution:I

    .line 18
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1;

    invoke-direct {v1, p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$1;-><init>(I)V

    sput-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    .line 40
    sget-object v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->mainHandler:Landroid/os/Handler;

    sget-object v2, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->watchdogRunnable:Ljava/lang/Runnable;

    const-wide/16 v3, 0x1388

    invoke-virtual {v1, v2, v3, v4}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 41
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda1;

    invoke-direct {v1, p0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda1;-><init>(I)V

    invoke-static {v1}, Lapp/morphe/extension/shared/utils/Logger;->printDebug(Lapp/morphe/extension/shared/utils/Logger$LogMessage;)V
    :try_end_20
    .catchall {:try_start_3 .. :try_end_20} :catchall_22

    .line 42
    monitor-exit v0

    return-void

    .line 14
    :catchall_22
    move-exception p0

    monitor-exit v0

    throw p0
.end method
