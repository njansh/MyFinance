'use client';

export function CardSkeleton() {
  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-3 animate-pulse">
      <div className="h-3 w-1/2 bg-slate-200 rounded-lg" />
      <div className="h-8 w-3/4 bg-slate-200 rounded-xl mt-1" />
    </div>
  );
}

export function MiniCardSkeleton() {
  return (
    <div className="p-4 bg-slate-50 border border-slate-200 rounded-xl flex justify-between items-center shadow-sm animate-pulse w-full">
      <div className="space-y-2 w-1/2">
        <div className="h-3 w-3/4 bg-slate-200 rounded-lg" />
        <div className="h-2 w-full bg-slate-200 rounded-lg" />
      </div>
      <div className="h-5 w-24 bg-slate-200 rounded-lg" />
    </div>
  );
}

export function ChartSkeleton() {
  return (
    <div className="p-6 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col h-[400px] animate-pulse">
      <div className="space-y-2 mb-6">
        <div className="h-4 w-1/3 bg-slate-200 rounded-lg" />
        <div className="h-3 w-1/2 bg-slate-200 rounded-lg" />
      </div>
      <div className="flex-1 w-full flex items-center justify-center">
        <div className="w-48 h-48 rounded-full border-[16px] border-slate-100 flex items-center justify-center">
          <div className="w-32 h-32 rounded-full bg-slate-50" />
        </div>
      </div>
    </div>
  );
}

export function DashboardSkeleton() {
  return (
    <div className="p-6 md:p-10 max-w-6xl mx-auto space-y-8">
      {/* Header Skeleton */}
      <div className="space-y-2 animate-pulse">
        <div className="h-7 w-48 bg-slate-200 rounded-xl" />
        <div className="h-4 w-80 bg-slate-200 rounded-lg" />
      </div>

      {/* KPIs Grid Skeleton */}
      <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
        <CardSkeleton />
        <CardSkeleton />
        <CardSkeleton />
      </div>

      {/* Secondary Grid Skeleton (Novo!) */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <MiniCardSkeleton />
        <MiniCardSkeleton />
      </div>

      {/* Charts Grid Skeleton */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ChartSkeleton />
        <ChartSkeleton />
      </div>
    </div>
  );
}