import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  'http://localhost:8080';

interface Params {
  params: Promise<{
    transactionId: string;
  }>;
}

export async function POST(
  request: NextRequest,
  { params }: Params
) {
  try {
    const { transactionId } = await params;

    const { actualAmount } =
      await request.json();

    const cookieStore = await cookies();

    const token =
      cookieStore.get('accessToken')?.value;

    if (!token) {
      return NextResponse.json(
        { error: 'UNAUTHORIZED' },
        { status: 401 }
      );
    }

    const response = await fetch(
      `${API_BASE_URL}/transactions/${transactionId}/confirm?actualAmount=${actualAmount}`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    );

    const data = await response.json();

    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error) {
    console.error(error);

    return NextResponse.json(
      { error: 'INTERNAL_ERROR' },
      { status: 500 }
    );
  }
}