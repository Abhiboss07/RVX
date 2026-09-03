.class public Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;
.super Ljava/lang/Object;
.source "AdvancedVideoQualityMenuPatch.java"


# static fields
.field private static final ADVANCED_VIDEO_QUALITY_MENU:Z

.field private static final ADVANCED_VIDEO_QUALITY_MENU_TYPE:Z

.field private static videoQualityBottomSheetRef:Ljava/lang/ref/WeakReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/WeakReference<",
            "Ljava/lang/Object;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public static synthetic $r8$lambda$Gc5RSl3bucmTKEPwSr2mTdRg-GU()Ljava/lang/String;
    .registers 1

    .line 137
    const-string v0, "onFlyoutMenuCreate failure"

    return-object v0
.end method

.method public static unblockViews(Landroid/view/View;)V
    .registers 4

    if-nez p0, :cond_3

    return-void

    :cond_3
    invoke-virtual {p0}, Landroid/view/View;->isEnabled()Z

    move-result v0

    const/4 v1, 0x1

    if-nez v0, :cond_d

    invoke-virtual {p0, v1}, Landroid/view/View;->setEnabled(Z)V

    :cond_d
    invoke-virtual {p0}, Landroid/view/View;->isClickable()Z

    move-result v0

    if-nez v0, :cond_16

    invoke-virtual {p0, v1}, Landroid/view/View;->setClickable(Z)V

    :cond_16
    instance-of v0, p0, Landroid/view/ViewGroup;

    if-eqz v0, :cond_30

    check-cast p0, Landroid/view/ViewGroup;

    invoke-virtual {p0}, Landroid/view/ViewGroup;->getChildCount()I

    move-result v0

    const/4 v1, 0x0

    :goto_21
    if-ge v1, v0, :cond_30

    invoke-virtual {p0, v1}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object v2

    invoke-static {v2}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->unblockViews(Landroid/view/View;)V

    add-int/lit8 v1, v1, 0x1

    goto :goto_21

    :cond_30
    return-void
.end method

.method public static synthetic $r8$lambda$IphM-XTZdm04uRboUV1MoR5aA-g(Landroid/support/v7/widget/RecyclerView;)V
    .registers 6

    :try_start_0
    invoke-static {p0}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->unblockViews(Landroid/view/View;)V

    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->ADVANCED_VIDEO_QUALITY_MENU:Z

    if-nez v0, :cond_a

    return-void

    :cond_a
    sget-boolean v0, Lapp/morphe/extension/youtube/patches/components/VideoQualityMenuFilter;->isVideoQualityMenuVisible:Z

    if-eqz v0, :cond_45

    invoke-virtual {p0}, Landroid/view/ViewGroup;->getChildCount()I

    move-result v0

    if-nez v0, :cond_15

    goto :goto_45

    :cond_15
    const/4 v0, 0x3

    invoke-static {p0, v0}, Lapp/morphe/extension/shared/utils/Utils;->getParentView(Landroid/view/View;I)Landroid/view/ViewParent;

    move-result-object v1

    instance-of v2, v1, Landroid/view/ViewGroup;

    if-eqz v2, :cond_45

    check-cast v1, Landroid/view/ViewGroup;

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object p0

    instance-of v3, p0, Landroid/view/ViewGroup;

    if-eqz v3, :cond_45

    check-cast p0, Landroid/view/ViewGroup;

    invoke-virtual {p0}, Landroid/view/ViewGroup;->getChildCount()I

    move-result v3

    const/4 v4, 0x4

    if-ge v3, v4, :cond_33

    goto :goto_45

    :cond_33
    invoke-virtual {p0, v0}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object p0

    if-nez p0, :cond_3a

    goto :goto_45

    :cond_3a
    invoke-static {p0}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->unblockViews(Landroid/view/View;)V

    invoke-virtual {p0}, Landroid/view/View;->callOnClick()Z

    move-result p0

    if-eqz p0, :cond_45

    const/16 p0, 0x8

    invoke-virtual {v1, p0}, Landroid/view/View;->setVisibility(I)V

    sput-boolean v2, Lapp/morphe/extension/youtube/patches/components/VideoQualityMenuFilter;->isVideoQualityMenuVisible:Z
    :try_end_45
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_45} :catch_46

    :cond_45
    :goto_45
    return-void

    :catch_46
    move-exception p0

    new-instance v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$$ExternalSyntheticLambda1;

    invoke-direct {v0}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$$ExternalSyntheticLambda1;-><init>()V

    invoke-static {v0, p0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    return-void
.end method

.method public static synthetic $r8$lambda$TGekHMsHQ0ugFFZ-XZPifsZu6RQ()Ljava/lang/String;
    .registers 1

    .line 87
    const-string v0, "dismissVideoQualityBottomSheet failure"

    return-object v0
.end method

.method public static bridge synthetic -$$Nest$sfgetADVANCED_VIDEO_QUALITY_MENU_TYPE()Z
    .registers 1

    .line 0
    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->ADVANCED_VIDEO_QUALITY_MENU_TYPE:Z

    return v0
.end method

.method public static bridge synthetic -$$Nest$smdismissVideoQualityBottomSheet()V
    .registers 0

    .line 0
    invoke-static {}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->dismissVideoQualityBottomSheet()V

    return-void
.end method

.method static constructor <clinit>()V
    .registers 2

    .line 23
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->ADVANCED_VIDEO_QUALITY_MENU:Lapp/morphe/extension/shared/settings/BooleanSetting;

    .line 24
    invoke-virtual {v0}, Lapp/morphe/extension/shared/settings/BooleanSetting;->get()Ljava/lang/Boolean;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    sput-boolean v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->ADVANCED_VIDEO_QUALITY_MENU:Z

    if-eqz v0, :cond_1c

    .line 25
    sget-object v0, Lapp/morphe/extension/youtube/settings/Settings;->ADVANCED_VIDEO_QUALITY_MENU_TYPE:Lapp/morphe/extension/shared/settings/BooleanSetting;

    .line 26
    invoke-virtual {v0}, Lapp/morphe/extension/shared/settings/BooleanSetting;->get()Ljava/lang/Boolean;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    if-eqz v0, :cond_1c

    const/4 v0, 0x1

    goto :goto_1d

    :cond_1c
    const/4 v0, 0x0

    :goto_1d
    sput-boolean v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->ADVANCED_VIDEO_QUALITY_MENU_TYPE:Z

    .line 27
    new-instance v0, Ljava/lang/ref/WeakReference;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->videoQualityBottomSheetRef:Ljava/lang/ref/WeakReference;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static addVideoQualityListMenuListener(Landroid/widget/ListView;)V
    .registers 2

    .line 44
    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->ADVANCED_VIDEO_QUALITY_MENU:Z

    if-nez v0, :cond_5

    return-void

    .line 46
    :cond_5
    new-instance v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;

    invoke-direct {v0, p0}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$1;-><init>(Landroid/widget/ListView;)V

    invoke-virtual {p0, v0}, Landroid/view/ViewGroup;->setOnHierarchyChangeListener(Landroid/view/ViewGroup$OnHierarchyChangeListener;)V

    return-void
.end method

.method private static dismissVideoQualityBottomSheet()V
    .registers 4

    .line 79
    sget-object v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->videoQualityBottomSheetRef:Ljava/lang/ref/WeakReference;

    invoke-virtual {v0}, Ljava/lang/ref/Reference;->get()Ljava/lang/Object;

    move-result-object v0

    .line 80
    sget-object v1, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->videoQualityBottomSheetRef:Ljava/lang/ref/WeakReference;

    invoke-virtual {v1}, Ljava/lang/ref/Reference;->clear()V

    if-nez v0, :cond_e

    return-void

    .line 84
    :cond_e
    :try_start_e
    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v1

    const-string v2, "dismiss"

    const/4 v3, 0x0

    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 85
    invoke-virtual {v1, v0, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_1c
    .catch Ljava/lang/Exception; {:try_start_e .. :try_end_1c} :catch_1d

    return-void

    :catch_1d
    move-exception v0

    .line 87
    new-instance v1, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$$ExternalSyntheticLambda0;

    invoke-direct {v1}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$$ExternalSyntheticLambda0;-><init>()V

    invoke-static {v1, v0}, Lapp/morphe/extension/shared/utils/Logger;->printException(Lapp/morphe/extension/shared/utils/Logger$LogMessage;Ljava/lang/Throwable;)V

    return-void
.end method

.method public static forceAdvancedVideoQualityMenuCreation(Z)Z
    .registers 2

    .line 97
    sget-boolean v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->ADVANCED_VIDEO_QUALITY_MENU:Z

    if-nez v0, :cond_9

    if-eqz p0, :cond_7

    goto :goto_9

    :cond_7
    const/4 p0, 0x0

    return p0

    :cond_9
    :goto_9
    const/4 p0, 0x1

    return p0
.end method

.method public static onFlyoutMenuCreate(Landroid/support/v7/widget/RecyclerView;)V
    .registers 3

    if-nez p0, :cond_3

    return-void

    :cond_3
    invoke-virtual {p0}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    move-result-object v0

    new-instance v1, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$$ExternalSyntheticLambda2;

    invoke-direct {v1, p0}, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch$$ExternalSyntheticLambda2;-><init>(Landroid/support/v7/widget/RecyclerView;)V

    invoke-virtual {v0, v1}, Landroid/view/ViewTreeObserver;->addOnDrawListener(Landroid/view/ViewTreeObserver$OnDrawListener;)V

    return-void
.end method

.method public static setVideoQualityBottomSheet(Ljava/lang/Object;)V
    .registers 2

    .line 35
    new-instance v0, Ljava/lang/ref/WeakReference;

    invoke-direct {v0, p0}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    sput-object v0, Lapp/morphe/extension/youtube/patches/video/AdvancedVideoQualityMenuPatch;->videoQualityBottomSheetRef:Ljava/lang/ref/WeakReference;

    return-void
.end method
