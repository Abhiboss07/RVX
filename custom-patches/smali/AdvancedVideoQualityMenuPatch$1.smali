.class Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;
.super Ljava/lang/Object;
.source "AdvancedVideoQualityMenuPatch.java"

# interfaces
.implements Landroid/view/ViewGroup$OnHierarchyChangeListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->addVideoQualityListMenuListener(Landroid/widget/ListView;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = null
.end annotation


# instance fields
.field final synthetic val$listView:Landroid/widget/ListView;


# direct methods
.method public static synthetic $r8$lambda$JDZLRekgSmEflkd6nhO22xYbH_4(Landroid/content/Context;)V
    .registers 1

    .line 59
    invoke-static {p0}, Lapp/morphe/extension/youtube/utils/VideoUtils;->showCustomVideoQualityFlyoutMenu(Landroid/content/Context;)V

    return-void
.end method

.method public static synthetic $r8$lambda$zbqx1ETphD_mJJmwksEkT9wCh8c()Ljava/lang/String;
    .registers 1

    .line 68
    const-string v0, "showAdvancedVideoQualityMenu failure"

    return-object v0
.end method

.method public constructor <init>(Landroid/widget/ListView;)V
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .line 46
    iput-object p1, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onChildViewAdded(Landroid/view/View;Landroid/view/View;)V
    .registers 6

    .line 51
    :try_start_0
    iget-object v0, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    invoke-virtual {v0, p2}, Landroid/view/ViewGroup;->indexOfChild(Landroid/view/View;)I

    move-result p2

    const/4 v0, 0x4

    if-eq p2, v0, :cond_a

    return-void

    :cond_a
    invoke-static {p1}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->unblockViews(Landroid/view/View;)V

    const/16 p2, 0x8

    .line 53
    invoke-virtual {p1, p2}, Landroid/view/View;->setVisibility(I)V

    .line 55
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->-$$Nest$sfgetADVANCED_VIDEO_QUALITY_MENU_TYPE()Z

    move-result p1

    if-eqz p1, :cond_31

    iget-object p1, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    invoke-virtual {p1}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object p1

    if-eqz p1, :cond_31

    .line 56
    iget-object p0, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object p0

    .line 57
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->-$$Nest$smdismissVideoQualityBottomSheet()V

    .line 58
    new-instance p1, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1$$ExternalSyntheticLambda0;

    invoke-direct {p1, p0}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1$$ExternalSyntheticLambda0;-><init>(Landroid/content/Context;)V

    const-wide/16 v0, 0x64

    invoke-static {p1, v0, v1}, Lapp/morphe/extension/shared/utils/Utils;->runOnMainThreadDelayed(Ljava/lang/Runnable;J)V

    return-void

    .line 64
    :cond_31
    iget-object p1, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    const/4 p2, 0x0

    invoke-virtual {p1, p2}, Landroid/view/View;->setSoundEffectsEnabled(Z)V

    .line 65
    iget-object p1, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    invoke-static {p1}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->unblockViews(Landroid/view/View;)V

    iget-object p0, p0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;->val$listView:Landroid/widget/ListView;

    const/4 p1, 0x0

    const-wide/16 v1, 0x0

    invoke-virtual {p0, p1, v0, v1, v2}, Landroid/widget/AdapterView;->performItemClick(Landroid/view/View;IJ)Z
    :try_end_3f
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_3f} :catch_40

    return-void

    :catch_40
    move-exception p0

    .line 68
    new-instance p1, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1$$ExternalSyntheticLambda1;

    invoke-direct {p1}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1$$ExternalSyntheticLambda1;-><init>()V

    invoke-static {p1, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    return-void
.end method

.method public onChildViewRemoved(Landroid/view/View;Landroid/view/View;)V
    .registers 3

    .line 0
    return-void
.end method
