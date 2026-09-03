.class public final synthetic Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Lapp/morphe/extension/shared/utils/Logger$LogMessage;


# instance fields
.field public final synthetic f$0:I


# direct methods
.method public synthetic constructor <init>(I)V
    .registers 2

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput p1, p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda0;->f$0:I

    return-void
.end method


# virtual methods
.method public final get()Ljava/lang/String;
    .registers 2

    .line 0
    iget v0, p0, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog$$ExternalSyntheticLambda0;->f$0:I

    invoke-static {v0}, Lapp/morphe/extension/youtube/patches/video/VideoQualityWatchdog;->lambda$notifyQualityChanged$1(I)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
